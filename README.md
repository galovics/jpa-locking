# Concurrency control with JPA
A very simple Spring Boot application with a couple of tests to show the Lost Update anomaly
and the possibilities to handle it with Optimistic Locking in JPA and Hibernate.

The application was written as a showcase for the following article:
https://blog.arnoldgalovics.com/optimistic-locking-in-jpa-hibernate/



## LostUpdateTest
The test shows the lost update anomaly. Two concurrent transactions are updating the very same
entity and the update made earlier is overwritten by the latter one.

## OptimisticLockingTest
The test contains 4 different use-cases

- Optimistic locking with versioning
- Optimistic locking with versioning when non-overlapping changes were made to the entity
- Versionless optimistic locking
- Versionless optimistic locking when non-overlapping changes were made to the entity