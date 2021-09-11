# binance-earn-check
Using current API to check if assets in spot wallet can be staked

Current version will get all available coins and check your current spot wallet balance of each asset.
Then it will get all available coins for flexible savings and check for each your current purchased amount.
Lastly it will get all available locked stacking products that are still available.
You will then get an overview which of your assets could be moved to flexible savings or to locked staking.
Curently there is no api to get the currently staked amount.

Future version will implement a web interface via vue and allow required tasks using the API
(for instance redeem flexible product and move it together with spot wallet balanced to a locked stacking position).
