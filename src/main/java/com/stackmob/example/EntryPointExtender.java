/**
 * Copyright 2012 StackMob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stackmob.example;

import com.stackmob.core.customcode.CustomCodeMethod;
import com.stackmob.core.jar.JarEntryObject;

import java.util.ArrayList;
import java.util.List;

public class EntryPointExtender extends JarEntryObject {

  @Override
  public List<CustomCodeMethod> methods() {
    List<CustomCodeMethod> list = new ArrayList<CustomCodeMethod>();
    list.add(new HelloWorld());
//    list.add(new TwilioSMS());
//    list.add(new Stripe());
    list.add(new SendGrid());
    list.add(new Increment());
    list.add(new SMPushRegisterDevice());
    
    
    // custom code test 
    list.add(new HighScore());
    list.add(new SetHighScore());
    list.add(new UserSelfFeed());
    list.add(new RecommendFollower());
    list.add(new UserGameInfo());
    list.add(new CharacterLikeFeed());
    list.add(new CharacterFeed());
    list.add(new CharacterSelfFeed());
    list.add(new CharacterInfo());
    
    list.add(new PostsWrite());
    list.add(new PostsLike());
    list.add(new PostsComment());
    
    list.add(new UserEmailInfo());
    
    list.add(new OAuthNaverConnect());
    list.add(new EventGetCoupon());
    
    
    return list;
  }

}
