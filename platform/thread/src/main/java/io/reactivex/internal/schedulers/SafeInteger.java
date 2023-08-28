package io.reactivex.internal.schedulers;

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午3:13
 */
class SafeInteger {

    static Integer getInteger(String propertyName, int defaultValue) {
        Integer result = defaultValue;
        try {
            result = Integer.getInteger(propertyName, defaultValue);
        } catch (NullPointerException ignore) {
        }
        return result;
    }

}
