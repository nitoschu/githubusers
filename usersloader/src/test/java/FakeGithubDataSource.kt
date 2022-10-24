import com.example.usersloader.GithubDataSource
import com.example.usersloader.GithubUser

class FakeGithubDataSource(
    val fakeResponse: Result<List<GithubUser>> = Result.success(listOf(mockGithubUser))
) : GithubDataSource {

    var page = -1
    var perPage = -1

    override suspend fun queryUsers(page: Int, perPage: Int): Result<List<GithubUser>> {
        this.page = page
        this.perPage = perPage
        return fakeResponse
    }
}

val mockGithubUser = GithubUser(
    id = 1,
    login = "Bert",
    nodeId = "abcd",
    avatarUrl = "abcd",
    gravatarId = "abcd",
    url = "abcd",
    htmlUrl = "abcd",
    followersUrl = "abcd",
    followingUrl = "abcd",
    gistsUrl = "abcd",
    starredUrl = "abcd",
    subscriptionsUrl = "abcd",
    organizationsUrl = "abcd",
    reposUrl = "abcd",
    eventsUrl = "abcd",
    receivedEventsUrl = "abcd",
    type = "user",
    siteAdmin = true,
    score = 1f,
    name = "Bert",
    company = "Bert Company",
    blog = "abcd",
    location = "abcd",
    email = "abcd",
    hireable = "false",
    bio = "abcd",
    publicRepos = 0,
    publicGists = 0,
    followers = 1,
    following = 1
)