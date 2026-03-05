package com.eunice.lib.reader.constant;

public enum SubScription {



    POWERSHELL("Microsoft-Windows-PowerShell/Operational"),
    SYSMON("Microsoft-Windows-Sysmon/Operational"),
    APPLICATION("Application"),
    SECURITY("Security"),
    SYSTEM("System");

    private final String serviceName;

    SubScription(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
