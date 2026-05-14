<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;

class AuthController extends Controller
{
    public function login(Request $request)
    {
        $email = $request->input('email');
        $password = $request->input('password');

        // Cari data user berdasarkan email dan pastikan dia adalah penghuni
        $user = User::where('email', $email)->where('role', 'penghuni')->first();

        if ($user && $user->password === $password) {
            return response()->json([
                'status' => 'success',
                'message' => 'Login berhasil!',
                'data' => $user
            ]);
        } else {
            return response()->json([
                'status' => 'error',
                'message' => 'Email atau password salah!'
            ], 401);
        }
    }
}