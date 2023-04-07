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
2. LOGGED_INT: this represents the "Main page" of the platform after the user logs in; from here they can view their data(stats, history, etc.), they can create/open/delete a post/playlist, or they can log out of their account.
3. WATCHING_POST: this represents a page where a post is open; here, a user can like/dislike the post, subscribe to the poster, leave a comment or reply, add the post to an existing playlist, delete the post(if they created it), or they can close the post, going back to the main page.
4. WATCHING_PLAYLIST: this represents a page where a playlist is open(TO BE IMPLEMENTED); here, a user can interact normally with the currently playing video in the playlist, while being able to reach another video in a sequential or random order. They can also delete the playlist or remove a video from it.
