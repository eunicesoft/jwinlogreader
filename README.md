# WinlogReader

- 윈도우 이벤트 로그를 실시간으로 구독하기 위한 라이브러리 입니다.


## Platform
- Windows 

## Language
- Java 21


## Dependencies
- jna
- jna-platform
- jackson-databind
- jackson-datatype-xml


## Usage
```java

WinlogReader winlogReader = WinlogReader.builder()
        .channel(SubScriptionChannel.create("Microsoft-Windows-PowerShell/Operational"))
        .channel(SubScriptionChannel.create("Microsoft-Windows-Sysmon/Operational", "*[System[(EventID=11)]]"))
        .channel(SubScriptionChannel.create("Application"))
        .channel(SubScriptionChannel.create("Security"))
        .channel(SubScriptionChannel.create("System"))
        .executor(Executors.newVirtualThreadPerTaskExecutor())
        .enableJson()
        .onEvent(new WinlogCallback() {
            @Override
            public void onEvent(String channel, String event) {
                System.out.println(channel + " : " + event);
            }

            @Override
            public void onError(String channel, WinlogError err) {
                System.err.println(channel + " : " + err);
            }

        })
        .build();

        winlogReader.start();
        Thread.sleep(100000 * 60);
        winlogReader.close();




```

## DOCS
[![Documentation](https://img.shields.io/badge/docs-latest-blue.svg)](https://eunicesoft.github.io/jwinlogreader/)