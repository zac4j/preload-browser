# preload-browser
Preload url content in Android WebView.

#### There are two screens in this scenario:
+ Main Screen, holds the url that wish to load and show in Browser Screen. 
+ Browser Screen, WebView host and wishes to show url content.

#### There are two approachs to preload url content:
+ The first approach is attempt to pre-initialize a WebView and preload url content:
  - The step is: get url in MainScreen -> initialize a WebView(via app context) -> load url content -> assemble in BrowserScreen(WebView host screen).

+ The second approach is attempt to load url content via network framework like OKHttp:
  - The step is: get url in MainScreen -> load url content via OKHttp and save it (in memory or local storage) -> post data to WebView in BrowserScreen.

**© 2018 Zac**

