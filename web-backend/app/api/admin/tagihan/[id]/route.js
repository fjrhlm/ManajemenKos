import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

// Hapus tagihan
export async function DELETE(request, { params }) {
  try {
    const { id } = await params;
    await supabase.from('tagihan').delete().eq('id_tagihan', id);
    return NextResponse.json({ status: 'success', message: 'Tagihan berhasil dihapus' });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
