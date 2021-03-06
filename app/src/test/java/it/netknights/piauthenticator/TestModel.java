/*
  privacyIDEA Authenticator

  Authors: Nils Behlen <nils.behlen@netknights.it>

  Copyright (c) 2017-2019 NetKnights GmbH

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package it.netknights.piauthenticator;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;

import it.netknights.piauthenticator.model.Model;
import it.netknights.piauthenticator.model.PushAuthRequest;
import it.netknights.piauthenticator.model.Token;

import static it.netknights.piauthenticator.utils.AppConstants.HOTP;
import static it.netknights.piauthenticator.utils.AppConstants.State.UNFINISHED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TestModel {

    @Test
    public void testInit() {
        // Empty init
        Model m = new Model();
        assertTrue(m.getTokens().isEmpty());

        // Init with elements
        ArrayList<Token> tokens = new ArrayList<>();
        Token token = Mockito.mock(Token.class);
        tokens.add(token);

        ArrayList<PushAuthRequest> requests = new ArrayList<>();
        PushAuthRequest req = Mockito.mock(PushAuthRequest.class);
        requests.add(req);

        Model m2 = new Model(tokens);
        assertEquals(token, m2.getTokens().get(0));

        Model m3 = new Model(null);
        assertTrue(m3.getTokens().isEmpty());
    }

    @Test
    public void checkForExpired() {
        Token token = new Token("serial", "label");
        assertThat(token.state, is(UNFINISHED));
        token.rollout_expiration = new Date();
        ArrayList<Token> list = new ArrayList<>();
        list.add(token);
        Model m3 = new Model(list);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String expired = m3.checkForExpiredTokens();
        // \n is appended to the expired tokens for formatting
        assertEquals("serial\n", expired);
        assertTrue(m3.getTokens().isEmpty());

        // message is null when there are no expired tokens
        Model m4 = new Model();
        assertNull(m4.checkForExpiredTokens());
    }

    @Test
    public void currentSelection() {
        Token token = new Token("lfknsw".getBytes(), "serial", "label", HOTP, 6);
        ArrayList<Token> list = new ArrayList<>();
        list.add(token);
        Model m = new Model(list);
        m.setCurrentSelection(0);
        assertEquals(token, m.getCurrentSelection());
        m.setCurrentSelection(-1);
        assertNull(m.getCurrentSelection());
    }

    @Test
    public void hasPushToken() {
        Token token = new Token("serial", "label");
        ArrayList<Token> list = new ArrayList<>();
        list.add(token);
        Model m = new Model(list);

        assertTrue(m.hasPushToken());

        m.getTokens().remove(0);

        assertFalse(m.hasPushToken());
    }

    @Test
    public void addingPushAuthRequests() {
        Token t = new Token("serial", "label");
        PushAuthRequest req = new PushAuthRequest("nonce", "url", "serial", "question", "title", "AAAAAAAA", 654321, true);

        assertTrue(t.addPushAuthRequest(req)); // Works the first time
        assertFalse(t.addPushAuthRequest(req)); // Adding duplicate does not work
    }
}
