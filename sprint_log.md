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
