<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Tagihan;

class TagihanController extends Controller
{
    public function getTagihanUser($id_user)
    {
        $tagihan = Tagihan::with(['user.kamar'])
                    ->where('id_user', $id_user)
                    ->where('status_bayar', 'Belum Bayar')
                    ->first();

        if ($tagihan) {
            return response()->json([
                'status' => 'success',
                'message' => 'Data tagihan berhasil diambil',
                'data' => $tagihan
            ]);
        } else {
            return response()->json([
                'status' => 'error',
                'message' => 'Tidak ada tagihan aktif',
                'data' => null
            ], 404);
        }
    }

    public function uploadBukti(Request $request, $id_tagihan)
    {
        $tagihan = Tagihan::find($id_tagihan);

        if (!$tagihan) {
            return response()->json(['status' => 'error', 'message' => 'Tagihan tidak ditemukan'], 404);
        }

        if ($request->hasFile('bukti_transfer')) {
            $file = $request->file('bukti_transfer');
            $filename = time() . '_' . $file->getClientOriginalName();
            
            $file->move(public_path('uploads'), $filename);

            $tagihan->bukti_transfer = 'uploads/' . $filename;
            $tagihan->status_bayar = 'Menunggu Validasi';
            $tagihan->save();

            return response()->json([
                'status' => 'success',
                'message' => 'Bukti pembayaran berhasil diupload',
                'data' => $tagihan
            ]);
        }

        return response()->json(['status' => 'error', 'message' => 'File bukti transfer tidak ditemukan'], 400);
    }
}