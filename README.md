# Video-Sharing-Platform
Java project that manages data regarding a video sharing platform.

Components:
- Post (any kind of post that a user might interact with)
  - Ad (a paid ad that promoted some kind of product; a user will not be attached to it, however it has a lifetime after which it will be deleted)
  - UserPost (a post that is visibly attached to a user)
    - CommunityPost (a post that is just a message possibly accompanied by an image)
    - Poll (a post that represents a text and several options a user can choose from)
    - Video (a post in the form of a video)
    - Short (a short clip from a video)
- User (any user that can interact with the platform)
- Comment (a comment that a user can leave on a UserPost)
- Playlist (a collection of videos a user can create)

Actions:

The project is split into several menus a user can interact with:
1. NOT_LOGGED: this is the menu that appears when a user is not logged in yet; here, they can either register a new account or log in an existing one.
2. LOGGED_IN: this represents the "Main page" of the platform after the user logs in; from here they can access different menus, or they can log out of their account.
3. POST_MENU: this is a menu that provides basic post functionality; showing, creating and opening posts.
4. WATCHING_POST: this represents a page where a post is open; here, a user can like/dislike the post, subscribe to the poster, leave a comment or reply, add the post to an existing playlist, or they can close the post, going back to the main page.
5. PLAYLIST_MENU: this is a menu that provides basic playlist functionality; showing, creating, opening and deleting one of your playlists.
6. WATCHING_PLAYLIST: this represents a page where a playlist is open; here, a user can traverse its videos in a sequential or random order, they can remove a video, or change the playlists name.
7. PROFILE: this represents the user profile; here, a user can access info about their subscribers/people they are subscribed to, they can modify their posts, they can access their post history, or they can update their account data or outright delete it.
