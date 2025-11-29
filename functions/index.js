const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendFriendRequestNotification = functions.firestore
  .document("users/{userId}")
  .onUpdate(async (change, context) => {
    const beforeData = change.before.data();
    const afterData = change.after.data();

    // Check if the friendRequests array has changed
    const beforeRequests = beforeData.friendRequests || [];
    const afterRequests = afterData.friendRequests || [];

    if (afterRequests.length > beforeRequests.length) {
      // A new friend request has been added
      const newRequestUids = afterRequests.filter(
        (uid) => !beforeRequests.includes(uid)
      );

      if (newRequestUids.length === 0) {
        console.log("No new friend requests found.");
        return null;
      }

      // We'll process the first new request. In a production app, you might want to handle multiple simultaneous requests.
      const fromUserId = newRequestUids[0];
      const toUserId = context.params.userId;

      // Get the sender's profile to get their username
      const fromUserDoc = await admin
        .firestore()
        .collection("users")
        .doc(fromUserId)
        .get();
      const fromUsername = fromUserDoc.data().username || "Someone";

      // Get the recipient's FCM token
      const toUserDoc = await admin
        .firestore()
        .collection("users")
        .doc(toUserId)
        .get();
      const fcmToken = toUserDoc.data().fcmToken;

      if (fcmToken) {
        const payload = {
          notification: {
            title: "New Friend Request",
            body: `${fromUsername} sent you a friend request.`,
          },
          token: fcmToken,
        };

        try {
          console.log("Sending notification to:", toUserId);
          await admin.messaging().send(payload);
          console.log("Notification sent successfully.");
        } catch (error) {
          console.error("Error sending notification:", error);
        }
      } else {
        console.log("User does not have a FCM token, cannot send notification.");
      }
    }
    return null;
  });
