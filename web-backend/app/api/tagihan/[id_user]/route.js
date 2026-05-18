import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

export async function GET(request, { params }) {
  try {
    const { id_user } = await params;

    // Ambil tagihan terbaru untuk user ini
    const { data: tagihan, error } = await supabase
      .from('tagihan').select('*').eq('id_user', id_user).order('id_tagihan', { ascending: false }).limit(1).single();

    if (error || !tagihan) {
      return NextResponse.json({ status: 'error', message: 'Belum ada tagihan' }, { status: 404 });
    }

    // Ambil info user + kamar
    const { data: user } = await supabase.from('users').select('nama, id_kamar').eq('id', tagihan.id_user).single();
    let kamar = null;
    if (user?.id_kamar) {
      const { data } = await supabase.from('kamar').select('nomor_kamar').eq('id_kamar', user.id_kamar).single();
      kamar = data;
    }

    // Format sesuai yang diharapkan Android (single object, bukan array)
    return NextResponse.json({
      status: 'success',
      data: {
        id_tagihan: tagihan.id_tagihan,
        bulan_tahun: tagihan.bulan_tahun,
        nominal: String(tagihan.nominal),
        status_bayar: tagihan.status_bayar,
        bukti_transfer: tagihan.bukti_transfer,
        user: {
          nama: user?.nama || '-',
          kamar: kamar
        }
      }
    });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
