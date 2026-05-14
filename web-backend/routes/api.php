<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\TagihanController;
use App\Http\Controllers\Api\AuthController;

// Rute bawaan Laravel (biarkan saja)
Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:sanctum');

// Rute GET (Hanya mengambil data)
Route::get('/tagihan/{id_user}', [TagihanController::class, 'getTagihanUser']);

// Rute POST (Mengirim data ke server)
Route::post('/login', [AuthController::class, 'login']);
Route::post('/tagihan/upload/{id_tagihan}', [TagihanController::class, 'uploadBukti']);