package com.fintek.upload_sdk.utils

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.fintek.upload_sdk.UploadUtils
import com.fintek.upload_sdk.model.UserAuthInfo
import java.util.ArrayList


object ContactUtil {

    fun getContacts(context: Context = UploadUtils.requiredContext): List<UserAuthInfo.UserContact>? {
        val contacts = ArrayList<UserAuthInfo.UserContact>()
        var cursor: Cursor? = null
        return try { //生成ContentResolver对象
            val contentResolver = context.contentResolver
            // 获得所有的联系人
            cursor =
                contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
            //这段代码和上面代码是等价的，使用两种方式获得联系人的Uri
            if (cursor == null) {
                return contacts
            }
            // 循环遍历
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val displayNameColumn =
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                do { // 获得联系人的ID
                    val contactId = cursor.getString(idColumn)
                    // 获得联系人姓名
                    val displayName = cursor.getString(displayNameColumn)

                    val lastTimeContacted =
                        cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED))
                    val sendToVoiceMail =
                        cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.SEND_TO_VOICEMAIL))

                    val inVisibleGroup =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP))
                    val starred =
                        cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.STARRED))
                    val isUserProfile =
                        cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.IS_USER_PROFILE))
                    val upTime =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP))
                    val timesContacted =
                        cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED))

                    // 查看联系人有多少个号码，如果没有号码，返回0
                    val phoneCount =
                        cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    if (phoneCount > 0) { // 获得联系人的电话号码列表
                        val phoneCursor = context.contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                            null,
                            null
                        )
                        if (phoneCursor != null) {
                            if (phoneCursor.moveToFirst()) {
                                do {
                                    val userContact = UserAuthInfo.UserContact(
                                        name = displayName,
                                        phone = phoneCursor.getString(
                                            phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                        ),
                                        id = contactId,
                                        hasPhoneNumber = phoneCount.toString(),
                                        inVisibleGroup = inVisibleGroup,
                                        isUserProfile = isUserProfile.toString(),
                                        lastTimeContacted = lastTimeContacted.toString(),
                                        sendToVoiceMail = sendToVoiceMail.toString(),
                                        starred = starred.toString(),
                                        timesContacted = timesContacted.toString(),
                                        upTime = upTime
                                    )
                                    if (!contacts.contains(userContact)) {
                                        contacts.add(userContact)
                                    }
                                } while (phoneCursor.moveToNext())
                            }
                            phoneCursor.close()
                        }
                    }
                } while (cursor.moveToNext())
            }
            contacts
        } catch (e: Exception) {
            null
        } finally {
            cursor?.close()
        }
    }
}