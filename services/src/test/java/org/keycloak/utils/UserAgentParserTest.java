/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
package org.keycloak.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for UserAgentParser
 */
public class UserAgentParserTest {

    @Test
    public void testIOS26Detection() {
        String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 26_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.1 Mobile/15E148 Safari/604.1";
        
        UserAgentParser.DeviceInfo deviceInfo = UserAgentParser.parseUserAgent(userAgent);
        
        assertEquals("iOS", deviceInfo.getOperatingSystem());
        assertEquals("26.1", deviceInfo.getOsVersion());
        assertEquals("Safari", deviceInfo.getBrowser());
        assertEquals("26.1", deviceInfo.getBrowserVersion());
        assertEquals("Mobile", deviceInfo.getDeviceType());
    }

    @Test
    public void testIOS26_1_1Detection() {
        String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 26_1_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.1.1 Mobile/15E148 Safari/604.1";
        
        UserAgentParser.DeviceInfo deviceInfo = UserAgentParser.parseUserAgent(userAgent);
        
        assertEquals("iOS", deviceInfo.getOperatingSystem());
        assertEquals("26.1.1", deviceInfo.getOsVersion());
        assertEquals("Safari", deviceInfo.getBrowser());
        assertEquals("26.1.1", deviceInfo.getBrowserVersion());
        assertEquals("Mobile", deviceInfo.getDeviceType());
    }

    @Test
    public void testIPadIOS26Detection() {
        String userAgent = "Mozilla/5.0 (iPad; CPU OS 26_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.1 Mobile/15E148 Safari/604.1";
        
        UserAgentParser.DeviceInfo deviceInfo = UserAgentParser.parseUserAgent(userAgent);
        
        assertEquals("iOS", deviceInfo.getOperatingSystem());
        assertEquals("26.1", deviceInfo.getOsVersion());
        assertEquals("Safari", deviceInfo.getBrowser());
        assertEquals("26.1", deviceInfo.getBrowserVersion());
        assertEquals("Tablet", deviceInfo.getDeviceType());
    }

    @Test
    public void testIOS18Detection() {
        String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 18_7 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.7 Mobile/15E148 Safari/604.1";
        
        UserAgentParser.DeviceInfo deviceInfo = UserAgentParser.parseUserAgent(userAgent);
        
        assertEquals("iOS", deviceInfo.getOperatingSystem());
        assertEquals("18.7", deviceInfo.getOsVersion());
        assertEquals("Safari", deviceInfo.getBrowser());
        assertEquals("18.7", deviceInfo.getBrowserVersion());
        assertEquals("Mobile", deviceInfo.getDeviceType());
    }

    @Test
    public void testAndroidDetection() {
        String userAgent = "Mozilla/5.0 (Linux; Android 14.0; SM-G998B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";
        
        UserAgentParser.DeviceInfo deviceInfo = UserAgentParser.parseUserAgent(userAgent);
        
        assertEquals("Android", deviceInfo.getOperatingSystem());
        assertEquals("14.0", deviceInfo.getOsVersion());
        assertEquals("Chrome", deviceInfo.getBrowser());
        assertEquals("Mobile", deviceInfo.getDeviceType());
    }

    @Test
    public void testWindowsDetection() {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        
        UserAgentParser.DeviceInfo deviceInfo = UserAgentParser.parseUserAgent(userAgent);
        
        assertEquals("Windows", deviceInfo.getOperatingSystem());
        assertEquals("10.0", deviceInfo.getOsVersion());
        assertEquals("Chrome", deviceInfo.getBrowser());
        assertEquals("Desktop", deviceInfo.getDeviceType());
    }

    @Test
    public void testMacOSDetection() {
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        
        UserAgentParser.DeviceInfo deviceInfo = UserAgentParser.parseUserAgent(userAgent);
        
        assertEquals("macOS", deviceInfo.getOperatingSystem());
        assertEquals("10.15.7", deviceInfo.getOsVersion());
        assertEquals("Chrome", deviceInfo.getBrowser());
        assertEquals("Desktop", deviceInfo.getDeviceType());
    }

    @Test
    public void testEmptyUserAgent() {
        UserAgentParser.DeviceInfo deviceInfo = UserAgentParser.parseUserAgent("");
        
        assertNull(deviceInfo.getOperatingSystem());
        assertNull(deviceInfo.getOsVersion());
        assertNull(deviceInfo.getBrowser());
        assertNull(deviceInfo.getBrowserVersion());
        assertNull(deviceInfo.getDeviceType());
    }

    @Test
    public void testNullUserAgent() {
        UserAgentParser.DeviceInfo deviceInfo = UserAgentParser.parseUserAgent(null);
        
        assertNull(deviceInfo.getOperatingSystem());
        assertNull(deviceInfo.getOsVersion());
        assertNull(deviceInfo.getBrowser());
        assertNull(deviceInfo.getBrowserVersion());
        assertNull(deviceInfo.getDeviceType());
    }
}