package com.example.password_manager.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class Account(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0L,
        val title: String?,
        val username: String?,
        val password: String?,
        @ColumnInfo(name = "package_name")
        val packageName: String?
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readLong(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeLong(id)
                parcel.writeString(title)
                parcel.writeString(username)
                parcel.writeString(password)
                parcel.writeString(packageName)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<Account> {
                override fun createFromParcel(parcel: Parcel): Account {
                        return Account(parcel)
                }

                override fun newArray(size: Int): Array<Account?> {
                        return arrayOfNulls(size)
                }
        }
}