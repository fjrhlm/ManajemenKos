import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

// Validasi pembayaran
export async function POST(request, { params }) {
  try {
    const { id } = await params;
    const { error } = await supabase.from('tagihan').update({ status_bayar: 'Lunas' }).eq('id_tagihan', id);
    if (error) throw error;
    return NextResponse.json({ status: 'success', message: 'Pembayaran berhasil divalidasi' });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
