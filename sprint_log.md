Sprint 1 - 10/March/2026

User Story:
As a user, I want the app to launch and authenticate
me so that I can securely access my account.

Work Done:
- Commit 1: build(di): create AppContainer and remove AppModule
  - Created manual AppContainer with all singleton dependencies
  - Deleted Hilt AppModule.kt

- Commit 2: refactor(app): remove @HiltAndroidApp from MeetMedsApplication
  - Replaced @HiltAndroidApp with manual AppContainer initialization
  - Application now owns and exposes the dependency container

Outcome:
- App builds without Hilt's component generation
- All dependencies are manually wired at app startup


Sprint 2 - 13/March/2026

User Story:
As a user, I want to log in and register so that
I can access my personalized medicine list.

Work Done:
- Commit 3: refactor(auth): remove @HiltViewModel from AuthViewModel
  - Removed @HiltViewModel and @Inject annotations
  - Added AuthViewModel.Factory for manual ViewModel creation
  - MainActivity now wires AuthViewModel using the factory

- Commit 4: refactor(cart): remove @HiltViewModel from CartViewModel
  - Removed @HiltViewModel and @Inject annotations
  - Added CartViewModel.Factory for manual ViewModel creation

Outcome:
- Auth screen launches and handles login/register without Hilt
- Cart ViewModel is manually initialized with its repository


Sprint 3 - 16/March/2026

User Story:
As a user, I want to browse medicines and add them
to my cart so that I can prepare my order.

Work Done:
- Commit 5: refactor(medicine): remove @HiltViewModel from MedicineListViewModel
  - Removed @HiltViewModel and @Inject annotations
  - Added MedicineListViewModel.Factory accepting MedicineRepository and CartRepository

- Commit 6: refactor(checkout): remove @HiltViewModel from CheckoutViewModel
  - Removed @HiltViewModel and @Inject annotations
  - Added CheckoutViewModel.Factory accepting CartRepository, OrderRepository and FirebaseAuth

Outcome:
- Medicine list screen loads and search works correctly
- Checkout screen initializes without Hilt injection


Sprint 4 - 20/March/2026

User Story:
As a user, I want to place orders and view my order
history so that I can track my medicine purchases.

Work Done:
- Commit 7: refactor(orders): remove @HiltViewModel from OrderHistoryViewModel
  - Removed @HiltViewModel and @Inject annotations
  - Added OrderHistoryViewModel.Factory accepting OrderRepository and AuthRepository

- Commit 8: refactor(data): remove @Inject constructors from all RepositoryImpl classes
  - Removed @Inject and javax.inject.Inject from AuthRepositoryImpl
  - Removed @Inject and javax.inject.Inject from UserRepositoryImpl
  - Removed @Inject and javax.inject.Inject from MedicineRepositoryImpl
  - Removed @Inject and javax.inject.Inject from CartRepositoryImpl
  - Removed @Inject and javax.inject.Inject from OrderRepositoryImpl

Outcome:
- Order history screen loads correctly with manual DI
- All repository implementations are plain constructor classes with no Hilt annotations



