class CoinStats implements Serializable {
	String coin
	String name
	double spot = 0
	double flex = 0
	String flexStatus
	double flexMin = 0
	boolean flexCanPurchase = false
	boolean flexCanRedeem = false
	double fixedLotsPurchased = 0
	int fixedLotsLowLimit = 0
	int fixedLotsUpLimit = 0

	List<CoinLock> projects = []

	CoinStats(String coin) {
		this.coin = coin
	}

	def double getTotal() {
		return this.flex + this.spot
	}

	def canSpotToFlex() {
		return (this.spot >= this.flexMin && this.flexCanPurchase)
	}

	def List<CoinLock> canTotalToLocked() {
		def total = this.spot + this.flex
		def result = false
		return this.projects.findAll {
			total >= it.min
		}
	}

	CoinStats updateSpot(AllCoins asset) {
		this.name = asset.name
		this.spot = asset.free.toDouble()
		return this
	}

	CoinStats updateFlex(FlexibleProduct flex, Double purchasedAmount) {
		this.flex = purchasedAmount ?: 0
		this.flexStatus = flex.status;
		this.flexMin = flex.minPurchaseAmount.toDouble()
		this.flexCanPurchase = flex.canPurchase
		this.flexCanRedeem = flex.canRedeem
		return this
	}

	CoinStats updateLocked(LockedStakingAsset project) {
		this.projects.push(new CoinLock(project))
		return this
	}
}

class CoinLock implements Serializable {
	Double min
	Double max
	Integer duration

	CoinLock(LockedStakingAsset project) {
		this.min = project.config.minPurchaseAmount.toDouble()
		this.max = project.config.maxPurchaseAmountPerUser.toDouble()
		this.duration = project.duration.toInteger()
	}
}
