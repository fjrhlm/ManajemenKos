<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Tagihan extends Model
{
    protected $table = 'tagihan';
    protected $primaryKey = 'id_tagihan';
    public $timestamps = false;
    protected $guarded = [];

    // Relasi: Satu tagihan ini milik satu user
    public function user()
    {
        return $this->belongsTo(User::class, 'id_user', 'id_user');
    }
}