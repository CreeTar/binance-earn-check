# binance-earn-check
Using current API to check if assets in spot wallet can be staked

Current version will get all available coins and check your current spot wallet balance of each asset.
Then it will get all available coins for flexible savings and check for each your current purchased amount.
Lastly it will get all available locked stacking products that are still available.
You will then get an overview which of your assets could be moved to flexible savings or to locked staking.
Curently there is no api to get the currently staked amount.

Future version will implement a web interface via vue and allow required tasks using the API
(for instance redeem flexible product and move it together with spot wallet balanced to a locked stacking position):

```
Assets that can be moved from Spot or Flex to Staking
-------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------------------------------
 Asset    | Spot              | Flex              | Total             | Total (EUR)| To Flex? | To Lock? | Locks                          |
-------------------------------------------------------------------------------------------------------------------------------------------
 Lock'able
-------------------------------------------------------------------------------------------------------------------------------------------
 BAKE     |               0.0 |        1.22224289 |        1.22224289 |     2.15 € | false    | true     | 60d min: 1.0, 30d min: 1.0     |
 BUSD     |        0.23430826 |               0.0 |        0.23430826 |      0.2 € | true     | false    |                                |
 CAKE     |               0.0 |        1.21089497 |        1.21089497 |    20.71 € | false    | true     | 60d min: 1.0, 30d min: 1.0     |
 DODO     |               0.0 |        1.20688914 |        1.20688914 |     1.57 € | false    | true     | 30d min: 1.0                   |
 EUR      |        0.26885184 |               0.0 |        0.26885184 |     0.27 € | true     | false    |                                |
-------------------------------------------------------------------------------------------------------------------------------------------
 Flex'able
-------------------------------------------------------------------------------------------------------------------------------------------
 BAKE     |               0.0 |        1.22224289 |        1.22224289 |     2.15 € | false    | true     | 60d min: 1.0, 30d min: 1.0     |
 BUSD     |        0.23430826 |               0.0 |        0.23430826 |      0.2 € | true     | false    |                                |
 CAKE     |               0.0 |        1.21089497 |        1.21089497 |    20.71 € | false    | true     | 60d min: 1.0, 30d min: 1.0     |
 DODO     |               0.0 |        1.20688914 |        1.20688914 |     1.57 € | false    | true     | 30d min: 1.0                   |
 EUR      |        0.26885184 |               0.0 |        0.26885184 |     0.27 € | true     | false    |                                |
-------------------------------------------------------------------------------------------------------------------------------------------
 Others
-------------------------------------------------------------------------------------------------------------------------------------------
-         |-                  |-                  |-                  |-           |-         |-         |-                               |
-------------------------------------------------------------------------------------------------------------------------------------------
```
