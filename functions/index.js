const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

const MASTER_ACCOUNT_UID = "dCArOSAJahUPt46I5ZULqn0GIOn2";

// New, more efficient function to automatically add the master account as a friend.
exports.addMasterAccountAsFriend = functions.firestore
  .document("users/{userId}")
  .onCreate(async (snap, context) => {
    const newUserId = context.params.userId;

    // Don't run this for the master account itself.
    if (newUserId === MASTER_ACCOUNT_UID) {
      console.log("Master account created, skipping friend add.");
      return null;
    }

    console.log(`New user created: ${newUserId}. Adding master account as a friend.`);

    // Get references to both user documents
    const masterUserRef = admin.firestore().collection("users").doc(MASTER_ACCOUNT_UID);
    const newUserRef = snap.ref;

    const batch = admin.firestore().batch();

    // 1. Add master account to the new user'''s friends list.
    batch.update(newUserRef, {
      friends: admin.firestore.FieldValue.arrayUnion(MASTER_ACCOUNT_UID),
    });

    // 2. Add the new user to the master account'''s friends list.
    batch.update(masterUserRef, {
      friends: admin.firestore.FieldValue.arrayUnion(newUserId),
    });

    try {
      await batch.commit();
      console.log("Successfully added master account as a friend.");
    } catch (error) {
      console.error("Error adding master account as friend:", error);
    }

    return null;
  });

exports.sendFriendRequestNotification = functions.firestore
  .document("users/{userId}")
  .onUpdate(async (change, context) => {
    const beforeData = change.before.data();
    const afterData = change.after.data();

    const beforeRequests = beforeData.friendRequests || [];
    const afterRequests = afterData.friendRequests || [];

    if (afterRequests.length > beforeRequests.length) {
      const newRequestUids = afterRequests.filter(
        (uid) => !beforeRequests.includes(uid)
      );

      if (newRequestUids.length === 0) {
        return null;
      }

      const fromUserId = newRequestUids[0];
      const toUserId = context.params.userId;

      const fromUserDoc = await admin.firestore().collection("users").doc(fromUserId).get();
      const fromUsername = fromUserDoc.data().username || "Someone";

      const toUserDoc = await admin.firestore().collection("users").doc(toUserId).get();
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
          await admin.messaging().send(payload);
        } catch (error) {
          console.error("Error sending notification:", error);
        }
      }
    }
    return null;
  });
