graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 1
    prc 4
    ant 6
    prb 4
    x 42
    y 44
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 5
    prb 2
    x 46
    y 27
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 8
    prb 5
    x 77
    y 92
  ]
  edge [
    source 0
    target 2
    bandwith 530
    delay 163
  ]
  edge [
    source 0
    target 3
    bandwith 676
    delay 404
  ]
  edge [
    source 0
    target 1
    bandwith 973
    delay 422
  ]
]
