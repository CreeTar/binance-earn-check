class LockedStaking implements Serializable {
	String code
	String message
	String messageDetail
	LockedStakingData[] data
	int total
	boolean success
}

class LockedStakingData implements Serializable {
	String asset
	String annualInterestRate
	String minPurchaseAmount
	String redeemPeriod
	String products
	LockedStakingAsset[] projects
}

class LockedStakingAsset implements Serializable {
	String id
	String projectId
	String asset
	String upLimit
	String purchased
	String endTime
	String issueStartTime
	String issueEndTime
	String duration
	String expectRedeemDate
	String interestPerUnit
	boolean withWhiteList
	boolean display
	String displayPriority
	String status
	boolean sellOut
	String createTimestamp
	LockedStakingAssetConfig config
}

class LockedStakingAssetConfig implements Serializable {
	String id
	String annualInterestRate
	String dailyInterestRate
	String extraInterestAsset
	String extraAnnualInterestRate
	String extraDailyInterestRate
	String minPurchaseAmount
	String maxPurchaseAmountPerUser
	String chainProcessPeriod
	String redeemPeriod
	String payInterestPeriod
}
