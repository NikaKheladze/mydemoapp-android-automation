# My Demo App — Android Test Automation

Mobile UI test automation framework for the Sauce Labs **My Demo App** (Android), built with
**Java 17, Maven, Appium (UiAutomator2), TestNG, Page Object Model + Page Factory** and **Allure** reporting.

---

## 1. Project Overview

The suite automates the core user journeys of the app — login/logout, navigation, product
details, cart management and the checkout shipping form — including negative and validation
scenarios. It is designed to be:

- **Readable** — tests describe user behaviour; locators and waits live in page objects.
- **Reliable** — explicit waits only (no implicit waits, no `Thread.sleep`), clean app state before every test.
- **Portable** — all environment-specific values are configurable; nothing machine-specific is committed.
- **Diagnosable** — every run produces an Allure report; failures carry a screenshot and the UI hierarchy.

## 2. Technologies

| Area | Technology | Version |
|---|---|---|
| Language / build | Java, Maven (wrapper included) | 17+, 3.9.16 |
| Mobile automation | Appium Java Client (`UiAutomator2Options`, `AndroidDriver`) | 10.1.1 |
| WebDriver API | Selenium (pinned via `selenium-bom`) | 4.42.0 |
| Test runner | TestNG | 7.12.0 |
| Reporting | Allure TestNG + Allure Maven plugin | 2.35.5 / 3.1.0 |
| Step instrumentation | AspectJ weaver (for Allure `@Step`) | 1.9.25.1 |
| Logging | SLF4J (simple binding in tests) | 2.0.17 |
| Appium server / driver | Appium, UiAutomator2 driver | 3.x / 8.x (tested against 3.8.0 / 8.7.0 docs) |

## 3. Prerequisites

| Requirement | Notes |
|---|---|
| **JDK 17 or newer** | `java -version`. `JAVA_HOME` must point to it. |
| **Maven** | Optional — use the included wrapper `./mvnw` (Windows: `mvnw.cmd`). |
| **Node.js** | Required by Appium 3: `^20.19`, `^22.12` or `>=24`. |
| **Appium 3** + **UiAutomator2 driver** | See [Appium Setup](#5-appium-setup). |
| **Android SDK** | Platform-tools (`adb`) and, for an emulator, the emulator + a system image. Easiest via Android Studio. |
| `ANDROID_HOME` | Must be set **in the shell that starts Appium** (e.g. `~/Library/Android/sdk`, `%LOCALAPPDATA%\Android\Sdk`). Add `$ANDROID_HOME/platform-tools` to `PATH`. |
| **Android emulator or device** | **API 34 or 35 with a standard 4 KB page size** (see §6). |

Verify the environment with the Appium doctor for the driver:

```bash
appium driver doctor uiautomator2
```

## 4. Application Setup

1. Download the APK from the releases page: <https://github.com/saucelabs/my-demo-app-android/releases>.
   The suite was written against **2.3.0** → asset `mda-2.3.0-27.apk`
   (use the plain `mda-…apk`, **not** `mda-androidTest-…apk`).
2. Put it in the project's `apps/` folder:

   ```
   apps/mda-2.3.0-27.apk
   ```

   `apps/*.apk` is git-ignored, so the binary is never committed.
3. Using another file name or location? Point `app.path` at it (relative to the project root or absolute):

   ```bash
   ./mvnw clean test -Dapp.path=/path/to/mda.apk        # or: export APP_PATH=/path/to/mda.apk
   ```

If the APK is missing, the run fails immediately with a message telling you exactly where it looked.
If the app is already installed on the device, you can skip the APK entirely with `-Dapp.path=`
(empty) — the app is then launched by package/activity.

## 5. Appium Setup

```bash
npm install -g appium                  # Appium 3 server
appium driver install uiautomator2     # Android driver
appium driver list --installed         # verify
appium                                 # start the server (default http://127.0.0.1:4723)
```

Keep the server running in its own terminal while the tests run. The server URL is configurable
(`appium.server.url`), so a remote server or a non-default port works too:
`appium --port 4724` + `-Dappium.server.url=http://127.0.0.1:4724`.

## 6. Android Emulator/Device Setup

**Emulator (recommended):**

1. Android Studio → *Device Manager* → create a device (e.g. *Pixel 8*) with an **API 35 or API 34
   "Google APIs x86_64"** system image.

   > ⚠️ Do **not** use images labelled **"16 KB Page Size"** or API 36+ images that default to 16 KB pages.
   > My Demo App is not 16 KB compatible, so Android shows a compatibility dialog on launch that blocks the tests.
   > Check with `adb shell getconf PAGE_SIZE` — it must print `4096`.
2. Start it from Android Studio, or from a terminal:

   ```bash
   emulator -list-avds
   emulator -avd <avd_name>
   ```

**Physical device:** enable *Developer options* → *USB debugging*, connect it and accept the RSA prompt.

Either way, confirm it is visible:

```bash
adb devices
# emulator-5554   device
```

With exactly one device online no further configuration is needed. With several, select one by serial:
`-Dudid=emulator-5554`.

### Running from an IDE

Open the folder that contains `pom.xml` in **IntelliJ IDEA** (Community edition is free), which has Maven
and TestNG support built in. **File → Open → select `pom.xml` → Open as Project**, set the project SDK
to JDK 17+ (**File → Project Structure**), and wait for the Maven import. Then use the green ▶ next to a
test class/method, or right-click `src/test/resources/testng.xml` → **Run**.

Android Studio is designed for Gradle-based Android apps and does not import Maven projects out of the box,
so use it for the emulator (Device Manager) and run the tests from IntelliJ IDEA or the terminal.

## 7. Configuration

Defaults live in [`src/test/resources/config/config.properties`](src/test/resources/config/config.properties).
Each key is resolved in this order — the **first defined value wins**:

1. JVM/Maven system property — `-Ddevice.name=Pixel_8`
2. Environment variable — `DEVICE_NAME=Pixel_8` (dots → underscores, upper case)
3. `src/test/resources/config/config.local.properties` — optional, **git-ignored**, for your machine
   (copy `config.local.properties.example`)
4. `config.properties` — committed defaults

An empty value means "not set" (e.g. `-Dapp.path=` disables APK installation).

| Property | Env variable | Default | Description |
|---|---|---|---|
| `appium.server.url` | `APPIUM_SERVER_URL` | `http://127.0.0.1:4723` | Appium server endpoint |
| `platform.name` | `PLATFORM_NAME` | `Android` | Platform |
| `automation.name` | `AUTOMATION_NAME` | `UiAutomator2` | Appium driver |
| `device.name` | `DEVICE_NAME` | `Android Emulator` | Descriptive device name |
| `platform.version` | `PLATFORM_VERSION` | *(empty)* | Optional Android version to target |
| `udid` | `UDID` | *(empty)* | Optional device serial (`adb devices`) when several are connected |
| `app.path` | `APP_PATH` | `apps/mda-2.3.0-27.apk` | APK to install; relative to project root or absolute |
| `app.package` | `APP_PACKAGE` | `com.saucelabs.mydemoapp.android` | Application id |
| `app.activity` | `APP_ACTIVITY` | `…view.activities.SplashActivity` | Launch activity |
| `app.wait.activity` | `APP_WAIT_ACTIVITY` | `…view.activities.*` | Activities Appium accepts as "app started" |
| `explicit.wait.timeout.seconds` | `EXPLICIT_WAIT_TIMEOUT_SECONDS` | `15` | Default explicit wait |
| `new.command.timeout.seconds` | `NEW_COMMAND_TIMEOUT_SECONDS` | `120` | Appium idle-session timeout |
| `server.install.timeout.seconds` | `SERVER_INSTALL_TIMEOUT_SECONDS` | `120` | UiAutomator2 server install timeout (slow emulators) |

No secrets are needed; the demo accounts are public test data built into the app.

## 8. Running Tests

With the Appium server running and a device online, from the project root:

```bash
./mvnw clean test                 # full suite (testng.xml)   — Windows: mvnw.cmd clean test
mvn clean test                    # same, with a locally installed Maven
```

Useful variations:

```bash
# One class / one method (overrides the suite file)
./mvnw clean test -Dtest=LoginTest
./mvnw clean test -Dtest=CartTest#cartShowsItemDetailsAndTotals

# Different device / server / APK
./mvnw clean test -Dudid=emulator-5556 -Dplatform.version=15
./mvnw clean test -Dappium.server.url=http://192.168.1.20:4723 -Dapp.path=/opt/apks/mda.apk
APPIUM_SERVER_URL=http://127.0.0.1:4724 ./mvnw clean test

# Compile only (no device needed) — what CI runs
./mvnw test-compile
```

A failing test fails the Maven build (as CI expects). The Allure results are written regardless,
so you can always generate the report afterwards.

## 9. Reporting

Every run writes raw Allure results to `target/allure-results/`.

```bash
./mvnw allure:report     # static report  -> target/site/allure-maven-plugin/index.html
./mvnw allure:serve      # generate + open in the browser via a local web server (Ctrl+C to stop)
```

- The report is generated as a **single self-contained `index.html`**, so it can be opened
  directly (double-click) or attached to a ticket/CI artifact.
- On first use the plugin downloads the Allure 3 runtime (and its own Node.js) into `.allure/`
  (git-ignored). Prefer the Java-based Allure 2 report? Add `-Dreport.version=2.36.0`.
- Allure shows each test with its description, feature, **page-level steps** (from `@Step` on page
  objects) and, for failures, a **screenshot** and the **UI hierarchy XML** at the moment of failure.
- TestNG's own reports are also produced in `target/surefire-reports/`.

## 10. Project Structure

```
├── apps/                                   # put the APK here (git-ignored)
├── src/main/java/com/mydemoapp/automation/
│   ├── config/    Config                   # layered config: -D > env > local file > defaults
│   ├── driver/    DriverManager            # per-thread session lifecycle (start/get/quit)
│   │              DriverFactory            # UiAutomator2Options from config, clear init errors
│   │              AppManager               # restart app into a clean state between tests
│   ├── model/     Credentials, ShippingAddress
│   ├── pages/     BasePage                 # Page Factory init, tap/type/reveal helpers
│   │              AppScreen                # shared header: menu, cart, cart badge
│   │              LoginPage, ProductsPage, ProductDetailsPage, CartPage, MenuPage,
│   │              LogoutDialog, CheckoutAddressPage, CheckoutPaymentPage
│   └── utils/     Waits                    # all explicit waits
│                  UiSelectors              # UiAutomator / UiScrollable locator builders
│                  TextParser, Screenshots
└── src/test/
    ├── java/com/mydemoapp/automation/
    │   ├── tests/      BaseTest + LoginTest, NavigationTest, CartTest, CheckoutTest, LogoutTest
    │   ├── listeners/  TestListener        # logging + failure screenshot/page source to Allure
    │   └── data/       TestUsers, Product, TestAddresses, ExpectedTexts
    └── resources/
        ├── config/config.properties (+ config.local.properties.example)
        ├── testng.xml, allure.properties, simplelogger.properties
```

## 11. Test Coverage

17 tests, each independent and each verifying an observable outcome.

| Class | Test | Verifies |
|---|---|---|
| **LoginTest** | `validUserCanLogIn` | Catalog shown after login; menu offers *Log Out* |
| | `lockedOutUserCannotLogIn` *(negative)* | "Sorry this user has been locked out."; stays on login |
| | `loginWithoutUsernameShowsValidationError` *(negative)* | "Username is required" |
| | `loginWithoutPasswordShowsValidationError` *(negative)* | "Enter Password" |
| **NavigationTest** | `appOpensOnProductCatalog` | Start screen, title "Products", catalog content |
| | `productTileOpensMatchingDetails` | Details show the tapped product's name and catalog price |
| | `emptyCartNavigatesBackToCatalog` | Empty-cart state ("No Items") and *Go Shopping* → catalog |
| | `menuNavigatesBetweenScreens` | Menu → Login (title) → Catalog |
| **CartTest** | `loggedInUserCanAddProductToCart` | Login → add → badge = 1 → product in cart |
| | `cartShowsItemDetailsAndTotals` | Per-item presence & unit price, item count, total price, badge |
| | `selectedQuantityIsReflectedInCart` | Quantity 2 on details → row quantity, item count, total |
| | `removingLastProductEmptiesCart` | Remove → empty state, badge cleared |
| **CheckoutTest** | `guestMustLogInBeforeCheckout` | Guest redirected to login, then continues to shipping form |
| | `emptyShippingFormShowsValidationErrors` *(negative)* | Messages for full name, address, city, zip; country error present |
| | `validShippingAddressContinuesToPayment` | Form submission accepted → payment step |
| **LogoutTest** | `userCanLogOut` | Confirmation message; login screen; menu offers *Log In* |
| | `cancellingLogoutKeepsUserLoggedIn` | Cancel keeps the session |

## 12. Design Decisions

- **TestNG** — class-level lifecycle (`@BeforeClass`/`@AfterClass`), `SoftAssert` for multi-field
  validations, suite XML with `configfailurepolicy="continue"` so one class's setup failure does not skip the others.
- **Session per class, app restart per test.** Creating a session (installing UiAutomator2
  server, launching the app) is the most expensive step, so it happens once per class. My Demo App
  keeps login and cart state *only in memory*, so `terminateApp` + `activateApp` before each test
  gives a guaranteed clean start (catalog, logged out, empty cart) — tests are independent and can run in any order.
- **`DriverManager` with `ThreadLocal`** — one place to create, access and quit the driver; ready for
  parallel runs on multiple devices without redesign. Session start failures are wrapped in
  `DriverInitializationException` with a checklist (server, driver, `adb devices`).
- **Waiting strategy** — no implicit wait. Page Factory uses `AppiumFieldDecorator(driver, Duration.ZERO)`
  (a single lookup attempt), and every wait is an explicit condition in `Waits` (visible, clickable,
  disappeared, "cart badge increased", "quantity became N"…). No `Thread.sleep` anywhere.
- **Locators** — resource-ids and accessibility ids first (Page Factory `@AndroidFindBy`).
  UiAutomator is used for text-identified list items and `UiScrollable` scrolling (capped at 8 swipes per
  search). Elements inside a specific list row (a product tile's image/price, a cart row's quantity/remove
  control) are found with short **relative XPath** anchored on the row's title, because
  `UiSelector.fromParent()` does not reliably stay inside the matched row. No absolute XPath.
- **Screen identity** — several resource-ids are reused across screens (e.g. `productTV` is the
  catalog title, the product name and the cart title), so each page is identified by an element
  unique to it (e.g. the catalog grid's accessibility label).
- **Header as a base class** — every in-app screen extends `AppScreen` (menu, cart, badge), so
  navigation reads naturally (`productsPage.openCart()`) without duplicated locators.
- **Money as `BigDecimal`** — prices are parsed from UI text and compared exactly.
- **Allure 3 report generated by the Maven plugin, results from `allure-testng` 2.35.5** — the
  adapter version is the one the plugin itself is built with; the plugin provisions its own runtime.
- **Dependency hygiene** — Selenium comes transitively from the Appium client and is pinned with
  `selenium-bom` to the client's baseline version for reproducible builds.

### Assumptions & app-specific findings

These came from reading the app's source (v2.3.0) and shaped the tests:

- **The app accepts any non-empty credentials** except the locked-out user. A "wrong password" test
  would therefore pass for the wrong reason; negative login coverage uses the locked-out user and the
  required-field validations instead.
- **The catalog crashes the app for most products** (intentional demo bug): tapping the tile at position 1
  or at position 6+ of the name-sorted catalog throws an exception. Only positions 0 and 2–5
  ("Sauce Labs Backpack" and its orange/red/violet/yellow variants) can be opened, so the tests use
  "Sauce Labs Backpack" and "Sauce Labs Backpack (orange)". Tapping e.g. "Sauce Labs Bike Light" crashes the app.
- **"Sauce Labs Bolt T-Shirt" has another intentional demo bug** (always adds 10 to the cart); it is not used either.
- The **country** validation message is truncated in the app ("Please provide your"), so only its presence is asserted.
- The empty-cart screen hides the cart badge entirely; the framework reports that as a count of 0.
- Checkout coverage stops at the payment step (entering card data adds little beyond the shipping-form scenarios).
- Tests run **sequentially** — one device serves one session at a time.
- CI (`.github/workflows/build.yml`) compiles the project only; running the suite requires an emulator and Appium server.
