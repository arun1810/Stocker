package com.example.stocker.pojo

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.stocker.model.room.type_converters.DateTypeConverter
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "Customer")
@TypeConverters(DateTypeConverter::class)
data class Customer(
    @PrimaryKey @ColumnInfo(name = "id")@NonNull val customerId:String,
    @ColumnInfo(name = "name")  @NonNull val name:String,
    @ColumnInfo(name = "password") @NonNull val password:String,
    @ColumnInfo(name = "gender") @NonNull val gender:Char,
    @ColumnInfo(name = "dob") @NonNull val dob:LocalDate,
    @ColumnInfo(name = "mobileNumber") @NonNull val mobile_number:String
    ):Serializable{

    override fun equals(other: Any?): Boolean {
        return super.equals(other)

    }

    override fun hashCode(): Int {
        var result = customerId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + dob.hashCode()
        result = 31 * result + mobile_number.hashCode()
        return result
    }
}
