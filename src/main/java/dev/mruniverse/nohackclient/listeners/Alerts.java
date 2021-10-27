package dev.mruniverse.nohackclient.listeners;

public enum Alerts implements Alert {
    NAME {
        public String getPath() {
            return "messages.checks.name.";
        }
        public String getName() { return "NAME"; }
    },
    VPN {
        public String getPath() {
            return "messages.checks.vpn.";
        }
        public String getName() { return "VPN"; }
    },
    SOCKS {
        public String getPath() {
            return "messages.checks.socks.";
        }
        public String getName() { return "SOCKS"; }
    },
    TOR {
        public String getPath() {
            return "messages.checks.tor.";
        }
        public String getName() { return "TOR"; }
    },
    SHADOW_SOCKS {
        public String getPath() {
            return "messages.checks.shadowsocks.";
        }
        public String getName() { return "SHADOW_SOCKS"; }
    },
    HTTP {
        public String getPath() {
            return "messages.checks.http.";
        }
        public String getName() { return "HTTP"; }
    },
    HTTPS {
        public String getPath() {
            return "messages.checks.https.";
        }
        public String getName() { return "HTTPS"; }
    },
    COMPROMISED_SERVER {
        public String getPath() {
            return "messages.checks.compromised-server.";
        }
        public String getName() { return "COMPROMISED_SERVER"; }
    },
    INFERENCE_ENGINE {
        public String getPath() { return "messages.checks.inference-engine."; }
        public String getName() { return "INFERENCE_ENGINE"; }
    },
    OPEN_VPN {
        public String getPath() {
            return "messages.checks.open-vpn.";
        }
        public String getName() { return "OPEN_VPN"; }
    },
    FRAUD_SCORE {
        public String getPath() {
            return "messages.checks.fraud-score.";
        }
        public String getName() { return "FRAUD_SCORE"; }
    },
    PAVLOV_MEDIA {
        public String getPath() {
            return "messages.checks.pavlov-media.";
        }
        public String getName() { return "PAVLOV_MEDIA"; }
    };




}
