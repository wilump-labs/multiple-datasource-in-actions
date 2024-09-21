# Spring 다중 datasource 설정 예제
datasource를 여러 개를 설정해서 개별 DB에 접근하는 방법

## 다중 datasource 설정 방법 외 함께 알면 좋은 내용
- 다중 datasource 설정 시 spring.jpa에 정의된 내용은 자동으로 적용되지 않음
  - 따라서 다중 datasource 설정 시 각 datasource에 대한 설정을 별도로 해주어야 함
- `JpaWebConfiguration`에 의해 설정되지 않기 때문에 `open-in-view` 설정이 되지 않음
  - OSIV 관련 블로그: https://velog.io/@cooper25_dev/%EC%BD%94%EB%93%9C%EB%A1%9C-%EB%B3%B4%EB%8A%94-OSIV-Open-Session-In-View

## 관련된 내용의 블로그
- https://velog.io/@suhongkim98/Spring-Data-JPA-multi-datasource-%EC%84%A4%EC%A0%95%ED%95%98%EA%B8%B0
- https://gwlabs.tistory.com/99
- https://datamoney.tistory.com/347
- https://velog.io/@ysh0917/JPA-Multiple-DataSource
- https://jiurinie.tistory.com/136
