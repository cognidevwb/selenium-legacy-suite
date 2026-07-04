# ACME Store — legacy Selenium E2E suite (demo)

A representative **brittle** end-to-end test suite: Selenium 4 + TestNG,
hand-rolled `ChromeDriver` lifecycle, Page Objects holding raw `By`
locators, and an explicit `WebDriverWait` before every assertion. It is the
input to the **AI-native test migration** playbook (target: code-grounded
Playwright + MCP).

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
