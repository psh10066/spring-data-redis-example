# Spring Data Redis 예제

1. `docker-compose up -d` 명령어를 실행하여 Redis 구동
2. DB Tool에 `jdbc:redis://localhost:6379` url 입력 후 Redis 구동 확인
3. Application 실행
4. `http/couponRequests.http` 파일을 이용하여 API 요청하며 쿠폰 캐시 적용 확인
    - 쿠폰 최초 조회 시 DB 조회 가정하여 3초 소요시간 발생 및 Redis 쿠폰 데이터 적재
    - 이후 동일 쿠폰 요청 시 Redis 캐시 적용되어 3초 소요시간 미발생
5. 부하 테스트 툴 이용하여 각 API의 동시성 처리 및 성능 확인
    - `issue` : 쿠폰 최대 발급 개수 제한 실패
    - `issue-lock` : 쿠폰 최대 발급 개수 제한 성공, 성능 하락
    - `issue-lua-script` : 쿠폰 최대 발급 개수 제한 성공, 성능 우수

## 참고 자료

- [9개 프로젝트로 경험하는
  대용량 트래픽 & 데이터 처리](https://fastcampus.co.kr/dev_online_traffic_data) > Project 3. 네고왕 이벤트 선착순 쿠폰 시스템