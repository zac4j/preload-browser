# preload-browser
Preload url content in Android WebView.

#### two screens:
+ **Screen A**: preload url content behind of screen. 
+ **Screen B**: WebView host and display url content.

#### two approachs:
+ **Plan A**: pre-initialize a WebView and preload url content:
  - The step is: initialize a WebView in ScreenA -> load url content -> assemble WebView in ScreenB.

+ **Plan B**: preload url content via OKHttp:
  - The step is: in ScreenA load url content via OKHttp and save it (in memory or local storage) -> post data to ScreenB's WebView.

