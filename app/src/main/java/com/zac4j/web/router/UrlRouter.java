package com.zac4j.web.router;

/**
 * Browser link & schema dispatch specs.
 * Created by Zaccc on 2018/1/19.
 */

public interface UrlRouter {

  boolean route(String scheme);

}
