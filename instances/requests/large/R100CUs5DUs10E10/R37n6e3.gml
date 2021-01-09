graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 4
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 5
    prb 6
    x 115
    y 32
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 8
    prb 4
    x 32
    y 19
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 9
    prb 6
    x 70
    y 94
  ]
  edge [
    source 0
    target 5
    bandwith 199
    delay 325
  ]
  edge [
    source 1
    target 4
    bandwith 778
    delay 307
  ]
  edge [
    source 2
    target 3
    bandwith 750
    delay 289
  ]
]
