import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

// GET - Ambil riwayat keluhan user (id = id_user)
export async function GET(request, { params }) {
  try {
    const { id } = await params;
    const { data, error } = await supabase.from('keluhan')
      .select('*').eq('id_user', id).order('id_keluhan', { ascending: false });
    if (error) throw error;
    return NextResponse.json({ status: 'success', data });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}

// DELETE - Hapus keluhan (id = id_keluhan)
export async function DELETE(request, { params }) {
  try {
    const { id } = await params;
    const { error } = await supabase.from('keluhan').delete().eq('id_keluhan', id);
    if (error) throw error;
    return NextResponse.json({ status: 'success', message: 'Keluhan berhasil dihapus' });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
