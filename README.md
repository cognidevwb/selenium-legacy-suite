# ACME Store — legacy Selenium E2E suite (demo)

A representative **brittle** end-to-end test suite: Selenium 4 + TestNG,
hand-rolled `ChromeDriver` lifecycle, Page Objects holding raw `By`
locators, and an explicit `WebDriverWait` before every assertion. It is the
input to the **AI-native test migration** playbook (target: code-grounded
Playwright + MCP).

## Running

Requires JDK 17+ and a live environment at `base.url`
(`src/test/resources/config.properties`). Browsers are provisioned by
WebDriverManager — no manual driver download.

```bash
mvn test                          # full suite (smoke + regression + legacy)
mvn test -Dbrowser=chrome         # explicit browser (default)
mvn test -Dbrowser=firefox        # the one other browser we support
mvn test -Dheadless=true          # CI mode
mvn test -Dgroups=smoke           # fast gate only
mvn test -Dgroups=regression      # nightly set
```

`mvn test-compile` works offline (no environment needed). Failure
screenshots land in `target/screenshots/` (only for tests extending
`BaseTest` — the two pre-2021 classes manage their own driver and get
none; known gap QA-1482).

## Layout

```
src/test/java/com/acme/tests/
├── support/           BaseTest, DriverFactory, ConfigReader,
│                      ScreenshotListener (PNG on failure), RetryAnalyzer
├── pageobjects/       raw-By page objects (Login, Checkout, Product,
│                      Cart, SearchResults, Account, OrderHistory)
└── *Test.java         TestNG classes; groups = smoke / regression
src/test/resources/
├── config.properties  base URL, timeouts, seeded QA account
└── testdata/declined-cards.csv   DataProvider input for declined-card matrix
```

## What makes it brittle (the migration worklist)

| Smell in this suite | What the migration does |
| --- | --- |
| `By.xpath("//button[@class='btn-primary submit']")` | → `getByRole('button', { name: 'Sign in' })` |
| `By.cssSelector("input[name='password']")` | → `getByLabel('Password')` |
| `WebDriverWait(...).until(visibilityOfElementLocated(...))` | **deleted** — Playwright auto-waits |
| `Assert.assertEquals(el.getText(), "Order placed")` | → `await expect(...).toHaveText('Order placed')` |
| manual `new ChromeDriver()` / `driver.quit()` | → Playwright fixtures |

## The flow being preserved

Two `@Test` flows — `loggedInUserCanPlaceAnOrder` and
`checkoutRejectsEmptyCardNumber`, plus `LoginTest` — each becomes a
declarative **intent spec** (goal + steps + assertions, locator-free) and
then a Playwright `*.spec.ts`. A traceability matrix proves every original
flow has a migrated counterpart, so coverage never silently regresses.
