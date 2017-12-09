/*
  privacyIDEA Authenticator

  Authors: Nils Behlen <nils.behlen@netknights.it>

  Copyright (c) 2017 NetKnights GmbH

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

import android.graphics.Color;
import android.widget.ProgressBar;

public class Token {

    private String currentOTP;
    private byte[] secret;
    private String label;
    private String type;
    private int digits;
    private int period;
    private String algorithm = "HmacSHA1"; //default is SHA1
    private int counter;
    private ProgressBar pb;
    private boolean withPIN = false;
    private boolean isLocked = false;
    private String Pin = "";
    private boolean withTapToShow = false;
    private boolean tapped = false;


    Token(byte[] secret, String label, String type, int digits) {
        this.label = label;
        this.secret = secret;
        this.type = type;
        this.digits = digits;
        this.period = 0;
        this.counter = 0;
    }

    void setTapped(boolean tapped) {
        this.tapped = tapped;
    }

    public boolean isTapped() {
        return tapped;
    }

    boolean isWithTapToShow() {
        return withTapToShow;
    }

    void setWithTapToShow(boolean withTapToShow) {
        this.withTapToShow = withTapToShow;
    }

    public String getPin() {
        return Pin;
    }

    public void setPin(String pin) {
        Pin = pin;
    }

    boolean isLocked() {
        return isLocked;
    }

    void setLocked(boolean locked) {
        isLocked = locked;
    }

    void setWithPIN(boolean withPIN) {
        this.withPIN = withPIN;
    }

    boolean isWithPIN() {
        return withPIN;
    }

    void setPb(ProgressBar pb) {
        if(this.pb!=null){
        if (this.pb.getId() == pb.getId()) {return;}}
        this.pb = pb;
        if(this.pb!=null){
            this.pb.setMax(getPeriod() * 100);
            this.pb.getProgressDrawable().setColorFilter(
                    Color.rgb(0x83, 0xc9, 0x27), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    ProgressBar getPb() {
        return pb;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    int getCounter() {
        return counter;
    }

    void setCounter(int counter) {
        this.counter = counter;
    }

    public void setSecret(byte[] secret) {
        this.secret = secret;
    }

    public byte[] getSecret() {
        return secret;
    }

    void setLabel(String label) {
        this.label = label;
    }

    String getLabel() {
        return label;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setAlgorithm(String algorithm) {
        // In the KeyURI the parameter is sha1/sha256/sha512, whereas the Mac instance is HmacSHA1 etc.
        if (algorithm.startsWith("sha") || algorithm.startsWith("SHA")) {
            this.algorithm = "Hmac" + algorithm.toUpperCase();
        } else if (algorithm.startsWith("Hmac")) {
            this.algorithm = algorithm;
        }
    }

    public int getDigits() {
        return digits;
    }

    public int getPeriod() {
        return period;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    String getCurrentOTP() {
        return currentOTP;
    }

    void setCurrentOTP(String currentOTP) {
        this.currentOTP = currentOTP;
    }
}

