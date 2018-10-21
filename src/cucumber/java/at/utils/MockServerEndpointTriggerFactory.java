package at.utils;
//TODO put factory stuff in own package
public class MockServerEndpointTriggerFactory {

    public static MockServerEndpointTrigger getTrigger(MockServerEndPointTriggerCriteria criteria) {
        switch(criteria) {
            case YESTERDAY: {
                return new MockServerEndPointTriggerForYesterday();
            }
            case LAST_TWO_DAYS: {
                return new MockServerEndPointTriggerForTheLastTwoDays();
            }
            default : {
                return new MockServerEndPointTriggerForTheLastMonth();
            }
        }
    }

}