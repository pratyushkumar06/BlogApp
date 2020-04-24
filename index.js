'use-strict'
const functions = require('firebase-functions');
const admin=require('firebase-admin');
admin.initializeApp();
exports.sendNotifications=functions.firestore.document("users/{user_id}/Notifications/{notification_id}").onWrite((change, context)=>{ //onWrite checks each time something is added to this
  const user_id=context.params.user_id;     //uid of receiver
  const notification_id=context.params.notification_id;

  console.log("UID:"+user_id+"  NotifId:"+notification_id);

 return admin.firestore().collection("users").doc(user_id).collection("Notifications").doc(notification_id).get().then(queryResult=>{
    const from_user_id=queryResult.data().id;    //The id that is stored in Notification collection
    const from_message=queryResult.data().message;  //The message stored in the database
    const from_data=admin.firestore().doc("users/"+from_user_id).get();
    const to_data=admin.firestore().doc("users/"+user_id).get();

    return Promise.all([from_data,to_data]).then(result=>{
        const from_name=result[0].data().name;
        const to_name=result[1].data().name;
        const token_id=result[1].data().token_id;
        //return console.log("From:"+from_name+"  To:"+to_name);

        const payload={
          notification:{
            title:"Notification From "+from_name,
            body :from_message,
            icon:"default",
            click_action:"com.Notifications.TARGETNOTIFICATION"
          },
          data: {    //data is sent separately
            message:from_message,
            from_id:from_name
          }
        };
        return admin.messaging().sendToDevice(token_id,payload).then(result=>{
          return console.log("Notification Sent");
        });
    });

  });


});
