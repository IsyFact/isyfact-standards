# 4.3.0

### Hinweise & bekannte Probleme

Ab dieser Version kann es zu einem Startfehler kommen, da reaktive Komponenten geladen werden.

**Lösung:**
In der `application.properties` oder `application.yml` den Web-Application-Type explizit auf `servlet` festlegen:

**application.properties:**
```properties
spring.main.web-application-type=servlet
```

**application.yml:**
```yaml
spring:
  main:
    web-application-type: servlet
```

Damit wird sichergestellt, dass die Application als klassische Servlet-basierte Webanwendung gestartet wird und die korrekten Beans verfügbar sind.
