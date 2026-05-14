<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Kamar extends Model
{
    protected $table = 'kamar'; // Nama tabel di database
    protected $primaryKey = 'id_kamar'; // Nama primary key kita
    public $timestamps = false; // Matikan fitur otomatis created_at / updated_at
    protected $guarded = []; // Izinkan semua kolom diisi data
}