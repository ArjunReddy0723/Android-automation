package com.taobao.tmts.framework.view;

import android.util.Log;

/**
 * @author shidun Custom module that is added to the WebView's JavaScript engine
 *         to enable callbacks to java code. This is required since WebView
 *         doesn't expose the underlying DOM. ʵ��java��javascript֮��Ķ��߳��첽ͨ�Ż��ơ�
 */
public final class JavascriptInterface {
	private volatile String result;
	private static volatile boolean resultReady;
	private static JavascriptInterface javascriptInterfaceInstance = new JavascriptInterface();
	private static long msecs = 1000;
	private static Integer count = 10;
	private TmtsWebView tmtsWebView;
	public void setTmtsWebView(TmtsWebView tmtsWebView) {
		this.tmtsWebView = tmtsWebView;
	}

	public JavascriptInterface() {
	}

	public static JavascriptInterface getInstance() {
		return javascriptInterfaceInstance;
	}

	/**
	 * @author shidun
	 * @return void ��ҳ��ִ�е�jsִ�лص�����¼����ֵ��
	 */
	public void excutejs(String updated) {
			result = updated;
			setReady(true);
			Log.d("Tmts", "CallBack excutejs: " + result);
	}
	
	public synchronized void setReady(boolean isReady){
		resultReady = isReady;
	}

	
	/**
	 * @author shidun A callback from JavaScript to Java that passes execution
	 *         result as a parameter.
	 * 
	 *         This method is accessible from WebView's JS DOM as
	 *         windows.webdriver.getResult().
	 * 
	 * @param result
	 *            Result that should be returned to Java code from WebView.
	 */
	public String getResult(){
		String result="";
		 try{
			 result = getResult(count);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
	
	public String getResult(long retryTimes) throws Exception {
	 
		Log.d("Tmts", "retryTimes:" + retryTimes+"resultReady: "+resultReady+"tmtsWebView:"+tmtsWebView.getWebView());
		long startTime = System.currentTimeMillis();
		long waitTime = msecs;
		if (resultReady)
			return doGet(); 
		else {
			for (;;) {
				Thread.sleep(100);
				if (resultReady){
					Log.d("Tmts", "resultReady is: "+resultReady+" need doGet()");
					return doGet();
				}else {
					waitTime = msecs - (System.currentTimeMillis() - startTime);
					if (waitTime <= 0){
						if(retryTimes>0){
							if(tmtsWebView!=null){
								tmtsWebView.excuteJs(tmtsWebView.getCurrentScript());
								Log.d("Tmts", "retryTimes2:" + retryTimes--);
								return getResult(retryTimes);
							}else{
								return "webView is null";
							}
						}else{
							return "out of waittime";
						}
					}
				}
			}
		}
		
	}
	private String doGet(){
		setReady(false);
		return result;
	}
}