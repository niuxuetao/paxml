package org.paxml.selenium.rc;

import com.thoughtworks.selenium.Selenium;

/**
 * This is a delegating proxy of the Selenium interface. 
 * 
 * @author Xuetao Niu
 *
 */
public abstract class AbstractSeleniumProxy implements Selenium {

	private Selenium sel;

	public AbstractSeleniumProxy(Selenium sel) {

		this.sel = sel;
	}

	public AbstractSeleniumProxy() {
		this(null);
	}

	public void setSelenium(Selenium sel) {
		this.sel = sel;
	}

	public Selenium getSelenium() {
		return sel;
	}

	public void setExtensionJs(String extensionJs) {
		sel.setExtensionJs(extensionJs);
	}

	public void start() {
		sel.start();
	}

	public void start(String optionsString) {
		sel.start(optionsString);
	}

	public void start(Object optionsObject) {
		sel.start(optionsObject);
	}

	public void stop() {
		sel.stop();
	}

	public void showContextualBanner() {
		sel.showContextualBanner();
	}

	public void showContextualBanner(String className, String methodName) {
		sel.showContextualBanner(className, methodName);
	}

	public void click(String locator) {
		sel.click(locator);
	}

	public void doubleClick(String locator) {
		sel.doubleClick(locator);
	}

	public void contextMenu(String locator) {
		sel.contextMenu(locator);
	}

	public void clickAt(String locator, String coordString) {
		sel.clickAt(locator, coordString);
	}

	public void doubleClickAt(String locator, String coordString) {
		sel.doubleClickAt(locator, coordString);
	}

	public void contextMenuAt(String locator, String coordString) {
		sel.contextMenuAt(locator, coordString);
	}

	public void fireEvent(String locator, String eventName) {
		sel.fireEvent(locator, eventName);
	}

	public void focus(String locator) {
		sel.focus(locator);
	}

	public void keyPress(String locator, String keySequence) {
		sel.keyPress(locator, keySequence);
	}

	public void shiftKeyDown() {
		sel.shiftKeyDown();
	}

	public void shiftKeyUp() {
		sel.shiftKeyUp();
	}

	public void metaKeyDown() {
		sel.metaKeyDown();
	}

	public void metaKeyUp() {
		sel.metaKeyUp();
	}

	public void altKeyDown() {
		sel.altKeyDown();
	}

	public void altKeyUp() {
		sel.altKeyUp();
	}

	public void controlKeyDown() {
		sel.controlKeyDown();
	}

	public void controlKeyUp() {
		sel.controlKeyUp();
	}

	public void keyDown(String locator, String keySequence) {
		sel.keyDown(locator, keySequence);
	}

	public void keyUp(String locator, String keySequence) {
		sel.keyUp(locator, keySequence);
	}

	public void mouseOver(String locator) {
		sel.mouseOver(locator);
	}

	public void mouseOut(String locator) {
		sel.mouseOut(locator);
	}

	public void mouseDown(String locator) {
		sel.mouseDown(locator);
	}

	public void mouseDownRight(String locator) {
		sel.mouseDownRight(locator);
	}

	public void mouseDownAt(String locator, String coordString) {
		sel.mouseDownAt(locator, coordString);
	}

	public void mouseDownRightAt(String locator, String coordString) {
		sel.mouseDownRightAt(locator, coordString);
	}

	public void mouseUp(String locator) {
		sel.mouseUp(locator);
	}

	public void mouseUpRight(String locator) {
		sel.mouseUpRight(locator);
	}

	public void mouseUpAt(String locator, String coordString) {
		sel.mouseUpAt(locator, coordString);
	}

	public void mouseUpRightAt(String locator, String coordString) {
		sel.mouseUpRightAt(locator, coordString);
	}

	public void mouseMove(String locator) {
		sel.mouseMove(locator);
	}

	public void mouseMoveAt(String locator, String coordString) {
		sel.mouseMoveAt(locator, coordString);
	}

	public void type(String locator, String value) {
		sel.type(locator, value);
	}

	public void typeKeys(String locator, String value) {
		sel.typeKeys(locator, value);
	}

	public void setSpeed(String value) {
		sel.setSpeed(value);
	}

	public String getSpeed() {
		return sel.getSpeed();
	}

	public String getLog() {
		return sel.getLog();
	}

	public void check(String locator) {
		sel.check(locator);
	}

	public void uncheck(String locator) {
		sel.uncheck(locator);
	}

	public void select(String selectLocator, String optionLocator) {
		sel.select(selectLocator, optionLocator);
	}

	public void addSelection(String locator, String optionLocator) {
		sel.addSelection(locator, optionLocator);
	}

	public void removeSelection(String locator, String optionLocator) {
		sel.removeSelection(locator, optionLocator);
	}

	public void removeAllSelections(String locator) {
		sel.removeAllSelections(locator);
	}

	public void submit(String formLocator) {
		sel.submit(formLocator);
	}

	public void open(String url, String ignoreResponseCode) {
		sel.open(url, ignoreResponseCode);
	}

	public void open(String url) {
		sel.open(url);
	}

	public void openWindow(String url, String windowID) {
		sel.openWindow(url, windowID);
	}

	public void selectWindow(String windowID) {
		sel.selectWindow(windowID);
	}

	public void selectPopUp(String windowID) {
		sel.selectPopUp(windowID);
	}

	public void deselectPopUp() {
		sel.deselectPopUp();
	}

	public void selectFrame(String locator) {
		sel.selectFrame(locator);
	}

	public boolean getWhetherThisFrameMatchFrameExpression(String currentFrameString, String target) {
		return sel.getWhetherThisFrameMatchFrameExpression(currentFrameString, target);
	}

	public boolean getWhetherThisWindowMatchWindowExpression(String currentWindowString, String target) {
		return sel.getWhetherThisWindowMatchWindowExpression(currentWindowString, target);
	}

	public void waitForPopUp(String windowID, String timeout) {
		sel.waitForPopUp(windowID, timeout);
	}

	public void chooseCancelOnNextConfirmation() {
		sel.chooseCancelOnNextConfirmation();
	}

	public void chooseOkOnNextConfirmation() {
		sel.chooseOkOnNextConfirmation();
	}

	public void answerOnNextPrompt(String answer) {
		sel.answerOnNextPrompt(answer);
	}

	public void goBack() {
		sel.goBack();
	}

	public void refresh() {
		sel.refresh();
	}

	public void close() {
		sel.close();
	}

	public boolean isAlertPresent() {
		return sel.isAlertPresent();
	}

	public boolean isPromptPresent() {
		return sel.isPromptPresent();
	}

	public boolean isConfirmationPresent() {
		return sel.isConfirmationPresent();
	}

	public String getAlert() {
		return sel.getAlert();
	}

	public String getConfirmation() {
		return sel.getConfirmation();
	}

	public String getPrompt() {
		return sel.getPrompt();
	}

	public String getLocation() {
		return sel.getLocation();
	}

	public String getTitle() {
		return sel.getTitle();
	}

	public String getBodyText() {
		return sel.getBodyText();
	}

	public String getValue(String locator) {
		return sel.getValue(locator);
	}

	public String getText(String locator) {
		return sel.getText(locator);
	}

	public void highlight(String locator) {
		sel.highlight(locator);
	}

	public String getEval(String script) {
		return sel.getEval(script);
	}

	public boolean isChecked(String locator) {
		return sel.isChecked(locator);
	}

	public String getTable(String tableCellAddress) {
		return sel.getTable(tableCellAddress);
	}

	public String[] getSelectedLabels(String selectLocator) {
		return sel.getSelectedLabels(selectLocator);
	}

	public String getSelectedLabel(String selectLocator) {
		return sel.getSelectedLabel(selectLocator);
	}

	public String[] getSelectedValues(String selectLocator) {
		return sel.getSelectedValues(selectLocator);
	}

	public String getSelectedValue(String selectLocator) {
		return sel.getSelectedValue(selectLocator);
	}

	public String[] getSelectedIndexes(String selectLocator) {
		return sel.getSelectedIndexes(selectLocator);
	}

	public String getSelectedIndex(String selectLocator) {
		return sel.getSelectedIndex(selectLocator);
	}

	public String[] getSelectedIds(String selectLocator) {
		return sel.getSelectedIds(selectLocator);
	}

	public String getSelectedId(String selectLocator) {
		return sel.getSelectedId(selectLocator);
	}

	public boolean isSomethingSelected(String selectLocator) {
		return sel.isSomethingSelected(selectLocator);
	}

	public String[] getSelectOptions(String selectLocator) {
		return sel.getSelectOptions(selectLocator);
	}

	public String getAttribute(String attributeLocator) {
		return sel.getAttribute(attributeLocator);
	}

	public boolean isTextPresent(String pattern) {
		return sel.isTextPresent(pattern);
	}

	public boolean isElementPresent(String locator) {
		return sel.isElementPresent(locator);
	}

	public boolean isVisible(String locator) {
		return sel.isVisible(locator);
	}

	public boolean isEditable(String locator) {
		return sel.isEditable(locator);
	}

	public String[] getAllButtons() {
		return sel.getAllButtons();
	}

	public String[] getAllLinks() {
		return sel.getAllLinks();
	}

	public String[] getAllFields() {
		return sel.getAllFields();
	}

	public String[] getAttributeFromAllWindows(String attributeName) {
		return sel.getAttributeFromAllWindows(attributeName);
	}

	public void dragdrop(String locator, String movementsString) {
		sel.dragdrop(locator, movementsString);
	}

	public void setMouseSpeed(String pixels) {
		sel.setMouseSpeed(pixels);
	}

	public Number getMouseSpeed() {
		return sel.getMouseSpeed();
	}

	public void dragAndDrop(String locator, String movementsString) {
		sel.dragAndDrop(locator, movementsString);
	}

	public void dragAndDropToObject(String locatorOfObjectToBeDragged, String locatorOfDragDestinationObject) {
		sel.dragAndDropToObject(locatorOfObjectToBeDragged, locatorOfDragDestinationObject);
	}

	public void windowFocus() {
		sel.windowFocus();
	}

	public void windowMaximize() {
		sel.windowMaximize();
	}

	public String[] getAllWindowIds() {
		return sel.getAllWindowIds();
	}

	public String[] getAllWindowNames() {
		return sel.getAllWindowNames();
	}

	public String[] getAllWindowTitles() {
		return sel.getAllWindowTitles();
	}

	public String getHtmlSource() {
		return sel.getHtmlSource();
	}

	public void setCursorPosition(String locator, String position) {
		sel.setCursorPosition(locator, position);
	}

	public Number getElementIndex(String locator) {
		return sel.getElementIndex(locator);
	}

	public boolean isOrdered(String locator1, String locator2) {
		return sel.isOrdered(locator1, locator2);
	}

	public Number getElementPositionLeft(String locator) {
		return sel.getElementPositionLeft(locator);
	}

	public Number getElementPositionTop(String locator) {
		return sel.getElementPositionTop(locator);
	}

	public Number getElementWidth(String locator) {
		return sel.getElementWidth(locator);
	}

	public Number getElementHeight(String locator) {
		return sel.getElementHeight(locator);
	}

	public Number getCursorPosition(String locator) {
		return sel.getCursorPosition(locator);
	}

	public String getExpression(String expression) {
		return sel.getExpression(expression);
	}

	public Number getXpathCount(String xpath) {
		return sel.getXpathCount(xpath);
	}

	public Number getCssCount(String css) {
		return sel.getCssCount(css);
	}

	public void assignId(String locator, String identifier) {
		sel.assignId(locator, identifier);
	}

	public void allowNativeXpath(String allow) {
		sel.allowNativeXpath(allow);
	}

	public void ignoreAttributesWithoutValue(String ignore) {
		sel.ignoreAttributesWithoutValue(ignore);
	}

	public void waitForCondition(String script, String timeout) {
		sel.waitForCondition(script, timeout);
	}

	public void setTimeout(String timeout) {
		sel.setTimeout(timeout);
	}

	public void waitForPageToLoad(String timeout) {
		sel.waitForPageToLoad(timeout);
	}

	public void waitForFrameToLoad(String frameAddress, String timeout) {
		sel.waitForFrameToLoad(frameAddress, timeout);
	}

	public String getCookie() {
		return sel.getCookie();
	}

	public String getCookieByName(String name) {
		return sel.getCookieByName(name);
	}

	public boolean isCookiePresent(String name) {
		return sel.isCookiePresent(name);
	}

	public void createCookie(String nameValuePair, String optionsString) {
		sel.createCookie(nameValuePair, optionsString);
	}

	public void deleteCookie(String name, String optionsString) {
		sel.deleteCookie(name, optionsString);
	}

	public void deleteAllVisibleCookies() {
		sel.deleteAllVisibleCookies();
	}

	public void setBrowserLogLevel(String logLevel) {
		sel.setBrowserLogLevel(logLevel);
	}

	public void runScript(String script) {
		sel.runScript(script);
	}

	public void addLocationStrategy(String strategyName, String functionDefinition) {
		sel.addLocationStrategy(strategyName, functionDefinition);
	}

	public void captureEntirePageScreenshot(String filename, String kwargs) {
		sel.captureEntirePageScreenshot(filename, kwargs);
	}

	public void rollup(String rollupName, String kwargs) {
		sel.rollup(rollupName, kwargs);
	}

	public void addScript(String scriptContent, String scriptTagId) {
		sel.addScript(scriptContent, scriptTagId);
	}

	public void removeScript(String scriptTagId) {
		sel.removeScript(scriptTagId);
	}

	public void useXpathLibrary(String libraryName) {
		sel.useXpathLibrary(libraryName);
	}

	public void setContext(String context) {
		sel.setContext(context);
	}

	public void attachFile(String fieldLocator, String fileLocator) {
		sel.attachFile(fieldLocator, fileLocator);
	}

	public void captureScreenshot(String filename) {
		sel.captureScreenshot(filename);
	}

	public String captureScreenshotToString() {
		return sel.captureScreenshotToString();
	}

	public String captureNetworkTraffic(String type) {
		return sel.captureNetworkTraffic(type);
	}

	public void addCustomRequestHeader(String key, String value) {
		sel.addCustomRequestHeader(key, value);
	}

	public String captureEntirePageScreenshotToString(String kwargs) {
		return sel.captureEntirePageScreenshotToString(kwargs);
	}

	public void shutDownSeleniumServer() {
		sel.shutDownSeleniumServer();
	}

	public String retrieveLastRemoteControlLogs() {
		return sel.retrieveLastRemoteControlLogs();
	}

	public void keyDownNative(String keycode) {
		sel.keyDownNative(keycode);
	}

	public void keyUpNative(String keycode) {
		sel.keyUpNative(keycode);
	}

	public void keyPressNative(String keycode) {
		sel.keyPressNative(keycode);
	}

}
