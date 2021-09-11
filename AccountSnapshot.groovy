class AccountSnapshot implements Serializable {
	int code
	String msg
	AccountSnapshotVos[] snapshotVos
}
class AccountSnapshotVos implements Serializable {
	String type
	long updateTime
	AccountBalances data
}
class AccountBalances implements Serializable {
	String totalAssetOfBtc
	AccountSnapshotAsset[] balances
}
class AccountSnapshotAsset implements Serializable {
	String asset
	String free
	String locked
}
