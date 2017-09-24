package solutions.theta.msbadmin.Parser;


import solutions.theta.msbadmin.Http.TaskResult;

public interface BaseParser {

	public static final int SUCCESS = 200;
	public static final String SUCCESS_TRUE = "200";

	public static final String KEY_ERROR_CODE = "code";
	public static final String KEY_ERROR_MESSAGE = "message";

	public TaskResult parse(int httpCode, String response);
}
