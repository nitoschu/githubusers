# Github Users

![screenshot](https://user-images.githubusercontent.com/28688458/198519746-9ba8a9b3-94f4-4e78-969d-2199eba771e0.png)


A little Android app that queries the Github search API for Github users with most followers. The users are then displayed in a list. 

To make it easy to check this project out and compile it, no authorization is used against the API. Therefore you might hit the 
[rate limit](https://docs.github.com/en/rest/rate-limit) of the API,especially when refreshing a large amount of pages. WHen the limit is reached, you need to wait a bit
until you can refresh the data. During this time you can still use the app and browser through the already loaded users.

The API updates the list of users very quickly (in my tests, within a second or two). This means that there is a possibility that users 
might be displayed multiple times, especially when waiting for a long time between fetching pages. For this project I decided to regard 
the API as a single source of truth, so this behavior is expected.

**Minimum SDK: 28 (Android 9)**

**Target SDK: 33 (Android 13)**

_To compile, JDK 11 is required._

