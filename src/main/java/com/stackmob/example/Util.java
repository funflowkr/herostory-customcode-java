package com.stackmob.example;

import java.net.HttpURLConnection;
import java.util.HashMap;
import com.stackmob.core.rest.ResponseToProcess;

/**
 * Created with IntelliJ IDEA.
 * User: sid
 * Date: 3/12/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */

public class Util {

  static public Boolean strCheck(String str) {
    boolean bool = true;

    if (str == null || str.isEmpty() ) {
      bool = false;
    }

   return bool;
  }
}
