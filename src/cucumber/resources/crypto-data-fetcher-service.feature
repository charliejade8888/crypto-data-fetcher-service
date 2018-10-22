Feature: Crypto Data Fetcher Service

  Scenario: fetching daily data for yesterday
    Given the following daily data is available for bitcoin yesterday:
      | time       | close   | high   | low     | open    | volumefrom | volumeto     |
      | 1529452800 | 6761.27 | 6817.9 | 6569.96 | 6741.28 | 59674.47   | 400530201.24 |
    When I make a request for daily data "BTC" from "2018-01-01" to "2018-01-01"
    Then the following data should be returned:
      | time       | close   | high   | low     | open    | volumefrom | volumeto     |
      | 2018/06/20 | 6761.27 | 6817.9 | 6569.96 | 6741.28 | 59674.47   | 400530201.24 |

  Scenario: fetching daily data for last week
    Given the following daily data is available for bitcoin over the last two days:
      | time       | close   | high   | low     | open    | volumefrom | volumeto     |
      | 1530748800 | 5761.00 | 6717.9 | 3569.96 | 6741.28 | 59674.47   | 500530201.24 |
      | 1530835200 | 4761.00 | 6717.9 | 2569.96 | 6741.28 | 59674.47   | 500530201.24 |
    When I make a request for daily data "BTC" from "2018-07-05" to "2018-07-06"
    Then the following data should be returned:
      | time       | close   | high   | low     | open    | volumefrom | volumeto     |
      | 2018/07/05 | 5761.00 | 6717.9 | 3569.96 | 6741.28 | 59674.47   | 500530201.24 |
      | 2018/07/06 | 4761.00 | 6717.9 | 2569.96 | 6741.28 | 59674.47   | 500530201.24 |

























#TODO add the following scenarios for logging - atom server not responding, hcm not responding, sending message on queue fails, updating db fails