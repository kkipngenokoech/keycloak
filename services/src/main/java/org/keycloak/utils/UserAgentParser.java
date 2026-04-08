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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing user agent strings to extract device and OS information.
 */
public class UserAgentParser {

    private static final Pattern IOS_PATTERN = Pattern.compile("OS (\\d+)_(\\d+)(?:_(\\d+))?");
    private static final Pattern SAFARI_VERSION_PATTERN = Pattern.compile("Version/(\\d+)\\.(\\d+)(?:\\.(\\d+))?");
    private static final Pattern ANDROID_PATTERN = Pattern.compile("Android (\\d+)\\.(\\d+)(?:\\.(\\d+))?");
    private static final Pattern WINDOWS_PATTERN = Pattern.compile("Windows NT (\\d+)\\.(\\d+)");
    private static final Pattern MAC_PATTERN = Pattern.compile("Mac OS X (\\d+)_(\\d+)(?:_(\\d+))?");

    public static class DeviceInfo {
        private String operatingSystem;
        private String osVersion;
        private String browser;
        private String browserVersion;
        private String deviceType;

        public DeviceInfo() {}

        public String getOperatingSystem() {
            return operatingSystem;
        }

        public void setOperatingSystem(String operatingSystem) {
            this.operatingSystem = operatingSystem;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        public String getBrowser() {
            return browser;
        }

        public void setBrowser(String browser) {
            this.browser = browser;
        }

        public String getBrowserVersion() {
            return browserVersion;
        }

        public void setBrowserVersion(String browserVersion) {
            this.browserVersion = browserVersion;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }
    }

    /**
     * Parse user agent string and extract device information.
     * 
     * @param userAgent the user agent string
     * @return DeviceInfo object containing parsed information
     */
    public static DeviceInfo parseUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return new DeviceInfo();
        }

        DeviceInfo deviceInfo = new DeviceInfo();

        // Detect iOS
        if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod")) {
            deviceInfo.setOperatingSystem("iOS");
            deviceInfo.setDeviceType(userAgent.contains("iPad") ? "Tablet" : "Mobile");
            
            // Parse iOS version from OS version in user agent
            Matcher iosMatcher = IOS_PATTERN.matcher(userAgent);
            if (iosMatcher.find()) {
                String major = iosMatcher.group(1);
                String minor = iosMatcher.group(2);
                String patch = iosMatcher.group(3);
                
                // For iOS 26.x, use the actual OS version instead of mapping to Safari version
                String version = major + "." + minor;
                if (patch != null && !patch.isEmpty()) {
                    version += "." + patch;
                }
                deviceInfo.setOsVersion(version);
            }
            
            // Parse Safari/browser version
            if (userAgent.contains("Safari")) {
                deviceInfo.setBrowser("Safari");
                Matcher safariMatcher = SAFARI_VERSION_PATTERN.matcher(userAgent);
                if (safariMatcher.find()) {
                    String major = safariMatcher.group(1);
                    String minor = safariMatcher.group(2);
                    String patch = safariMatcher.group(3);
                    
                    String version = major + "." + minor;
                    if (patch != null && !patch.isEmpty()) {
                        version += "." + patch;
                    }
                    deviceInfo.setBrowserVersion(version);
                }
            }
        }
        // Detect Android
        else if (userAgent.contains("Android")) {
            deviceInfo.setOperatingSystem("Android");
            deviceInfo.setDeviceType("Mobile");
            
            Matcher androidMatcher = ANDROID_PATTERN.matcher(userAgent);
            if (androidMatcher.find()) {
                String major = androidMatcher.group(1);
                String minor = androidMatcher.group(2);
                String patch = androidMatcher.group(3);
                
                String version = major + "." + minor;
                if (patch != null && !patch.isEmpty()) {
                    version += "." + patch;
                }
                deviceInfo.setOsVersion(version);
            }
            
            if (userAgent.contains("Chrome")) {
                deviceInfo.setBrowser("Chrome");
            }
        }
        // Detect Windows
        else if (userAgent.contains("Windows")) {
            deviceInfo.setOperatingSystem("Windows");
            deviceInfo.setDeviceType("Desktop");
            
            Matcher windowsMatcher = WINDOWS_PATTERN.matcher(userAgent);
            if (windowsMatcher.find()) {
                String major = windowsMatcher.group(1);
                String minor = windowsMatcher.group(2);
                deviceInfo.setOsVersion(major + "." + minor);
            }
            
            if (userAgent.contains("Edge")) {
                deviceInfo.setBrowser("Edge");
            } else if (userAgent.contains("Chrome")) {
                deviceInfo.setBrowser("Chrome");
            } else if (userAgent.contains("Firefox")) {
                deviceInfo.setBrowser("Firefox");
            }
        }
        // Detect macOS
        else if (userAgent.contains("Mac OS X")) {
            deviceInfo.setOperatingSystem("macOS");
            deviceInfo.setDeviceType("Desktop");
            
            Matcher macMatcher = MAC_PATTERN.matcher(userAgent);
            if (macMatcher.find()) {
                String major = macMatcher.group(1);
                String minor = macMatcher.group(2);
                String patch = macMatcher.group(3);
                
                String version = major + "." + minor;
                if (patch != null && !patch.isEmpty()) {
                    version += "." + patch;
                }
                deviceInfo.setOsVersion(version);
            }
            
            if (userAgent.contains("Safari")) {
                deviceInfo.setBrowser("Safari");
            } else if (userAgent.contains("Chrome")) {
                deviceInfo.setBrowser("Chrome");
            } else if (userAgent.contains("Firefox")) {
                deviceInfo.setBrowser("Firefox");
            }
        }

        return deviceInfo;
    }
}