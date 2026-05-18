import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

export async function GET(request, { params }) {
  try {
    const { id_user } = await params;

    const { data: tagihan, error } = await supabase
      .from('tagihan').select('*').eq('id_user', id_user).order('id_tagihan', { ascending: false });
    if (error) throw error;

    // Ambil info user + kamar untuk setiap tagihan
    const result = [];
    for (const t of tagihan) {
      const { data: user } = await supabase.from('users').select('nama, id_kamar').eq('id', t.id_user).single();
      let kamar = null;
      if (user?.id_kamar) {
        const { data } = await supabase.from('kamar').select('nomor_kamar').eq('id_kamar', user.id_kamar).single();
        kamar = data;
      }
      result.push({ ...t, id_tagihan: t.id_tagihan, user: { nama: user?.nama, kamar } });
    }

    return NextResponse.json({ status: 'success', data: result });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
