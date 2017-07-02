# Scouter
RealTimeScouter
 ------------------------------------------
|    한이음 실시간 성능 모니터링 시스템    |
 ------------------------------------------

# 진행 방향 및 해야할 일

# Email(보고서)과 Line(실시간 알림)의 2가지 방향으로 나아갈 것.

 * Email 기능 ----> 보고서 기능으로 확장
 * Line  알림 ----> 주력 실시간 알림 기능

 1. Email 기능 보완
   1) 똑같은 시스템 이슈 메일 여러번 보내지 않게 하기.
   2) 좀 더 상세한 내용 포함
   3) 참고 링크: https://github.com/scouter-project/scouter-plugin-server-alert-email

 2. Line 실시간 알림 모듈 개발
   1) 간결한 내용을 포함한 실시간 알림 위주 기능 구현
   2) 참고 링크: https://github.com/scouter-project/scouter-plugin-server-alert-line

 3. 기존 시간제어모듈에 쓰이던 HashMap<Integer, long[]>을 HashMap<Integer, Container>로 바꿈
 4. Container 클래스 내부에는 기존에 사용되던 long[] values = new long[5];와, 새로 추가된 String name; 이 있음
   1) name은 XLog등에서 요청 페이지별로 응답시간 측정하기 위해 넣어놓은 것.

 -> 3, 4번 소스코드만 수정함
 => 서버 빌드, 이메일 빌드, 스카우터 서버에서 테스트 해봐야됨
