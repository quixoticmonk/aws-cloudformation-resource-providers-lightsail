package software.amazon.lightsail.bucket;

import software.amazon.cloudformation.proxy.StdCallbackContext;
import software.amazon.cloudformation.proxy.delay.Constant;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@lombok.EqualsAndHashCode(callSuper = true)
public class CallbackContext extends StdCallbackContext {

    public static final Constant BACKOFF_DELAY = Constant.of().delay(Duration.ofSeconds(30))
            .timeout(Duration.ofMinutes(25)).build();

    public static String PRE_CHECK_CREATE = "preCheckCreate";
    public static String POST_CHECK_CREATE = "postCheckCreate";
    public static String PRE_CHECK_UPDATE = "preCheckUpdate";
    public static String PRE_CHECK_DELETE = "preCheckDelete";
    public static String PRE_TAG_UPDATE = "preCheckUpdateTag";
    public static String PRE_CHECK_DETACH = "preCheckDetach";
    public static String PRE_CHECK_ATTACH = "preCheckAttach";
    public static String POST_CHECK_DETACH = "postCheckDetach";
    public static String POST_CHECK_ATTACH = "postCheckAttach";

    private Map<String, Boolean> isPreCheckDone = new HashMap<>();

    private Map<String, Integer> waitCount = new HashMap<>();

    private Integer maxWaitCount = 40;

    private Integer postOperationWaitCount = 1;

    public int getWaitCount(final String key) {
        return this.waitCount.getOrDefault(key, 0);
    }

    public void incrementWaitCount(final String key) {
        this.waitCount.put(key, getWaitCount(key) + 1);
    }

    public boolean isWaitCountReached(final String key) {
        int maxWait = this.maxWaitCount;
        if (key.equalsIgnoreCase(POST_CHECK_CREATE) || key.equalsIgnoreCase(POST_CHECK_DETACH) || key.equalsIgnoreCase(POST_CHECK_ATTACH)) {
            maxWait = this.postOperationWaitCount;
        }
        return this.getWaitCount(key) >= maxWait;
    }

    public boolean getIsPreCheckDone(final String key) {
        return this.isPreCheckDone.getOrDefault(key, false);
    }

}
