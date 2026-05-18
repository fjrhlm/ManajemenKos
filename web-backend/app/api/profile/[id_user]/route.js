import { supabase } from '@/lib/supabase';
import { NextResponse } from 'next/server';

export async function GET(request, { params }) {
  try {
    const { id_user } = await params;
    const { data: user, error } = await supabase
      .from('users').select('id, nama, email, role, id_kamar').eq('id', id_user).single();
    if (error || !user) return NextResponse.json({ status: 'error', message: 'User tidak ditemukan' }, { status: 404 });

    let kamar = null;
    if (user.id_kamar) {
      const { data } = await supabase.from('kamar').select('nomor_kamar').eq('id_kamar', user.id_kamar).single();
      kamar = data;
    }

    return NextResponse.json({
      status: 'success',
      data: { id_user: user.id, nama: user.nama, email: user.email, role: user.role, kamar }
    });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}

export async function PUT(request, { params }) {
  try {
    const { id_user } = await params;
    const { nama, email } = await request.json();

    const { data, error } = await supabase.from('users')
      .update({ nama, name: nama, email }).eq('id', id_user).select().single();
    if (error) throw error;

    return NextResponse.json({ status: 'success', message: 'Profil berhasil diperbarui', data });
  } catch (e) {
    return NextResponse.json({ status: 'error', message: e.message }, { status: 500 });
  }
}
