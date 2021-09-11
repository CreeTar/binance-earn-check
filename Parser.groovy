//file:noinspection GroovyInstanceMethodNamingConvention
import groovy.json.JsonSlurper
import org.apache.commons.httpclient.HttpClient

class Parser {
	def useRealtime = true
	def endpoint = "https://api.binance.com"
	def key = System.getenv("BINANCE_API_KEY")
	def secret = System.getenv("BINANCE_API_SECRET")

	// 30 days limit or 100 entries
	def api_Savings_InterestHistory = "/sapi/v1/lending/union/interestHistory"
	def api_Account_Snapshot = "/sapi/v1/accountSnapshot"
	def api_All_Coins = "/sapi/v1/capital/config/getall"
	def api_Flexible_Product_List = "/sapi/v1/lending/daily/product/list"
	def api_Flexible_Token_Position = "/sapi/v1/lending/daily/token/position"
	def api_Fixed_Product_List = "/sapi/v1/lending/project/list"
	def api_Fixed_Position = "/sapi/v1/lending/project/position/list"

	def client = new HttpClient()

	AccountSnapshot parseAccountSnapshot() {
		def url = endpoint + api_Account_Snapshot
		def params = []
		params << "type=SPOT" // [MARGIN|FUTURES]
		params << "limit=1"
		def data = addTimeAndSigAndGet(url, params)
		return new JsonSlurper().parseText(data) as AccountSnapshot
	}

	AllCoins[] parseAllCoins() {
		def url = endpoint + api_All_Coins
		def data = addTimeAndSigAndGet(url, [])
		return new JsonSlurper().parseText(data) as AllCoins[]
	}

	FlexibleProduct[] parseFlexibleProductList() {
		def url = endpoint + api_Flexible_Product_List
		def data = addTimeAndSigAndGet(url, ["size=50"])
		return new JsonSlurper().parseText(data) as FlexibleProduct[]
	}

	FlexibleProductPosition[] parseFlexibleProductPosition(String asset) {
		def url = endpoint + api_Flexible_Token_Position
		def data = addTimeAndSigAndGet(url, ["asset=$asset"])
		return new JsonSlurper().parseText(data) as FlexibleProductPosition[]
	}

	FixedProduct[] parseFixedProductList() {
		return (parseFixedProductLists(false) + parseFixedProductLists(true))
	}

	private FixedProduct[] parseFixedProductLists(boolean activity) {
		def url = endpoint + api_Fixed_Product_List

		def params = []
		params << "size=100"
		params << "status=ALL"
		params << "type=${(activity) ? 'ACTIVITY': 'CUSTOMIZED_FIXED'}"
		def data = addTimeAndSigAndGet(url, params)

		return new JsonSlurper().parseText(data) as FixedProduct[]
	}

	FixedPosition[] parseFixedPositionList(String asset) {
		def url = endpoint + api_Fixed_Position
		def data = addTimeAndSigAndGet(url, ["asset=${asset}"])
		return new JsonSlurper().parseText(data) as FixedPosition[]
	}

	LockedStaking parseLockedStaking() {
		def url = 'https://www.binance.com/bapi/earn/v1/friendly/pos/union?pageSize=100&pageIndex=1&status=ALL&asset='
		return new JsonSlurper().parse(new URL(url)) as LockedStaking
	}

	private String addTimeAndSigAndGet(String url, List<String> params) {
		params << Common.createBinanceTimestamp()
		params << Common.createBinanceSignature(params, secret)

		def call = url + "?" + params.join('&')
		return Common.binanceGet(client, call, key)
	}

	def printWithPadding(List<Object> data, List<Integer> padding) {
		data.eachWithIndex { Object entry, int idx ->
			print entry.toString().padRight(padding[idx]) + "|"
		}
		println()
	}

	def getObjects(String filePath, Closure action) {
		if (useRealtime) {
			return Common.serializeObject(action(), filePath)
		} else {
			Common.deserializeObject(filePath) ?:  Common.serializeObject(action(), filePath)
		}
	}

	def handleAllCoins(Map<String, CoinStats> coinStats) {
		def filePath = './all_coins.obj'
		AllCoins[] data = getObjects(filePath, { parseAllCoins() })

		def padding = [15, 15, 15, 15, 15]
		println "=" * padding.sum()
		println filePath
		println "-" * padding.sum()

		printWithPadding(['Coin', 'Free', 'Stored', 'Locked', 'Trading'], padding)

		data.sort{ it.coin }.each {entry ->
			def stats = coinStats.getOrDefault(entry.coin, new CoinStats(entry.coin))
			coinStats.put(entry.coin, stats.updateSpot(entry))
			if (entry.free.toDouble() > 0) {
				printWithPadding([entry.coin, entry.free, entry.storage, entry.locked, entry.trading], padding)
			}
		}
		println "=" * padding.sum()
	}

	def handleFlexProd(Map<String, CoinStats> coinStats) {
		def filePath = './flexible_products.obj'
		FlexibleProduct[] data = getObjects(filePath, { parseFlexibleProductList() })

		def padding = [15, 20, 15, 15, 15]
		println "=" * padding.sum()
		println filePath
		println "-" * padding.sum()

		printWithPadding(['Coin', 'Purchased', 'Status'], padding)

		data.sort{ it.asset }.each {entry ->
			FlexibleProductPosition[] flexPos = (useRealtime) ? parseFlexibleProductPosition(entry.asset) : []
			def purchasedAmount = flexPos.sum { it.totalAmount.toDouble() }

			def stats = coinStats.getOrDefault(entry.asset,  new CoinStats(entry.asset))
			coinStats.put(entry.asset, stats.updateFlex(entry, purchasedAmount))

			printWithPadding([entry.asset, purchasedAmount ?: '-', entry.status], padding)
		}
		println "=" * padding.sum()
	}

	def handleLockedStacking(Map<String, CoinStats> coinStats) {
		def filePath = './available_locked_staking.obj'
		LockedStaking data = getObjects(filePath, { parseLockedStaking() })

		def padding = [10, 30, 15, 10, 15]
		println "=" * padding.sum()
		println filePath
		println "-" * padding.sum()

		printWithPadding(['Coin', 'Min-Max', 'Status', 'Duration', 'Interest'], padding)
		data.data.sort{ it.asset }.each {entry ->
			def stats = coinStats.getOrDefault(entry.asset, null)
			if (stats) {
				entry.projects.each { project ->
					def config = project.config
					if (!project.sellOut) {
						coinStats.put(entry.asset, stats.updateLocked(project))
						printWithPadding([entry.asset, config.minPurchaseAmount.replace('.00000000', '') + "-" + config.maxPurchaseAmountPerUser.replace('.00000000', ''), project.status, project.duration, config.annualInterestRate], padding)
					}
				}
			}
		}
		println "=" * padding.sum()
	}

	@org.junit.Test
	void testApi_Binance_Earn() {
		def coinStats = [:] as Map<String, CoinStats>

		handleAllCoins(coinStats)
		println()
		handleFlexProd(coinStats)
		println()
		handleLockedStacking(coinStats)
		println()
		println "Assets that can be moved from Spot\\Flex to Staking"
		println "-" * 100

		def padding = [10, 19, 19, 19, 10, 10, 20]
		def header = []
		header << "Asset"
		header << "Spot"
		header << "Flex"
		header << "Total"
		header << "to Flex?"
		header << "to Lock?"
		header << "Locks"
		println "-" * 100
		printWithPadding(header, padding)

		coinStats.sort().each {
			def data = []
			data << it.key
			data << it.value.spot.round(10)
			data << it.value.flex.round(10)
			data << (it.value.total).round(10)
			data << it.value.canSpotToFlex()
			def locked = it.value.canTotalToLocked()
			data << (locked.size() > 0)
			def projects = locked.findResults {"${it.duration}d min: ${it.min}" }
			data << projects.join(', ')
			if (it.value.total > 0 && it.value.canSpotToFlex() || locked.size() > 0) {
				printWithPadding(data, padding)
			}
		}
		println "*" * 100
  }
}
